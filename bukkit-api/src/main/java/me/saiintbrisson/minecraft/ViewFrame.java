package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.saiintbrisson.minecraft.feature.Feature;
import me.saiintbrisson.minecraft.feature.FeatureInstaller;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class ViewFrame implements CompatViewFrame<ViewFrame> {

	private static final String BSTATS_SYSTEM_PROPERTY = "inventory-framework.enable-bstats";

	@EqualsAndHashCode.Include
	private final UUID id = UUID.randomUUID();

	@EqualsAndHashCode.Include
	@NotNull
	private final Plugin owner;

	private ViewErrorHandler errorHandler;

	@ToString.Exclude
	private final Map<Class<? extends AbstractView>, AbstractView> views = new HashMap<>();

	@Getter(AccessLevel.NONE)
	@ToString.Exclude
	private final Map<Class<? extends View>, View> legacyViews = new HashMap<>();

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Listener listener;

	private BiConsumer<PaginatedViewContext<?>, ViewItem> previousPageItem, nextPageItem;

	@Getter(AccessLevel.NONE)
	private final FeatureInstaller<ViewFrame> featureInstaller = new DefaultFeatureInstaller<>(this);

	static {
		PlatformUtils.setFactory(new BukkitViewComponentFactory());
	}

	/**
	 * Creates a new ViewFrame instance.
	 *
	 * @param owner The Bukkit plugin holder of this view framework.
	 * @deprecated Use {@link #of(Plugin, AbstractView...)} instead.
	 */
	@Deprecated
	public ViewFrame(@NotNull Plugin owner) {
		this.owner = owner;
	}

	@Override
	public AbstractView get(@NotNull Player player) {
		final Inventory inventory = player.getOpenInventory().getTopInventory();

		// fast path -- check for inventory type first
		if (inventory.getType() == InventoryType.PLAYER) return null;

		// TODO: search for alternative ways to retrieve current view because
		//       InventoryHolder is deprecated in newer versions of Bukkit API
		final InventoryHolder holder = inventory.getHolder();
		if (!(holder instanceof AbstractView)) return null;

		return (AbstractView) holder;
	}

	/**
	 * All registered views.
	 *
	 * @return An unmodifiable collection of all registered views.
	 * @deprecated Will be removed soon.
	 */
	@Deprecated
	public Map<Class<? extends View>, View> getRegisteredViews() {
		return Collections.unmodifiableMap(legacyViews);
	}

	@Override
	public ViewFrame with(@NotNull AbstractView... views) {
		synchronized (getViews()) {
			for (final AbstractView view : views) {
				if (getViews().containsKey(view.getClass()))
					throw new IllegalArgumentException(String.format(
						"View %s already registered, try to use #with before #register instead.",
						view.getClass().getName()));

				getViews().put(view.getClass(), view);

				if (view instanceof View) legacyViews.put(((View) view).getClass(), (View) view);
			}
		}
		return this;
	}

	@Override
	public ViewFrame remove(@NotNull AbstractView... views) {
		synchronized (getViews()) {
			for (final AbstractView view : views) {
				view.close();
				getViews().remove(view.getClass());
			}
		}
		return this;
	}

	/**
	 * Registers a view.
	 *
	 * @param views All views that'll be registered.
	 * @deprecated Use {@link #register()} and {@link #with(AbstractView...)} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
	public void register(@NotNull AbstractView... views) {
		with(views);
		register();
	}

	@Override
	public ViewFrame register() {
		if (isRegistered())
			throw new IllegalStateException("This ViewFrame is already registered.");

		final ServicesManager servicesManager = getOwner().getServer().getServicesManager();

		// check if there's another ViewFrame instance in the same server, enable metrics once
		if (!servicesManager.isProvidedFor(ViewFrame.class)) {
			enableMetrics();
		}

		for (final AbstractView view : views.values()) {
			try {
				view.setViewFrame(this);
				view.init();
				PlatformUtils.getFactory().setupView(view);
				owner.getLogger().info("\"" + view.getClass().getSimpleName() + "\" registered");
			} catch (final Exception e) {
				throw new RuntimeException(
					"Failed to register view: " + view.getClass().getName(), e);
			}
		}

		final Plugin plugin = getOwner();
		plugin.getServer().getPluginManager().registerEvents(listener = new ViewListener(plugin, this), plugin);
		servicesManager.register(ViewFrame.class, this, plugin, ServicePriority.Normal);
		return this;
	}

	private void enableMetrics() {
		final boolean metricsEnabled =
			Boolean.parseBoolean(System.getProperty(BSTATS_SYSTEM_PROPERTY, Boolean.TRUE.toString()));

		if (!metricsEnabled) return;

		try {
			new Metrics((JavaPlugin) getOwner(), 15518);
		} catch (final Exception ignored) {
		}
	}

	@Override
	public void unregister() {
		if (!isRegistered()) throw new IllegalStateException("Not registered");

		final Iterator<AbstractView> viewIterator = views.values().iterator();
		while (viewIterator.hasNext()) {
			final AbstractView view = viewIterator.next();
			view.close();
			viewIterator.remove();
		}

		HandlerList.unregisterAll(listener);
		listener = null;
	}

	@Override
	public boolean isRegistered() {
		return listener != null;
	}

	@Override
	public @NotNull ViewComponentFactory getFactory() {
		return PlatformUtils.getFactory();
	}

	@Override
	public <T extends AbstractView> @NotNull T open(@NotNull Class<T> viewClass, @NotNull Player player) {
		return open(viewClass, player, Collections.emptyMap());
	}

	@Override
	public <T extends AbstractView> @NotNull T open(
		@NotNull Class<T> viewClass, @NotNull Player player, @NotNull Map<String, Object> data) {
		final Viewer viewerImpl;
		try {
			viewerImpl = PlatformUtils.getFactory().createViewer(player);
		} catch (final Throwable e) {
			throw new RuntimeException("Failed to create viewer implementation to current platform.", e);
		}

		return open(viewClass, viewerImpl, data);
	}

	@Override
	public <T extends AbstractView> @NotNull T open(
		@NotNull Class<T> viewClass, @NotNull Viewer viewer, Map<String, Object> data) {
		if (!isRegistered())
			throw new IllegalStateException("Attempt to open a view without having registered the view frame.");

		@SuppressWarnings("unchecked") final T view = (T) views.get(viewClass);

		if (view == null)
			throw new IllegalStateException(String.format("View %s is not registered.", viewClass.getName()));

		getOwner().getServer().getScheduler().runTaskLater(getOwner(), () -> view.open(viewer, data), 1L);
		return view;
	}

	/**
	 * Registers a view.
	 *
	 * @param view The view that'll be registered.
	 * @deprecated Use {@link #with(AbstractView...)} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
	public void addView(final AbstractView view) {
		with(view);
	}

	/**
	 * Registers a view.
	 *
	 * @param views All views that'll be registered.
	 * @deprecated Use {@link #with(AbstractView...)} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
	public void addView(final AbstractView... views) {
		with(views);
	}

	@Override
	public @NotNull Plugin getPlatform() {
		return getOwner();
	}

	@Override
	public Collection<Feature<?, ?>> getInstalledFeatures() {
		return featureInstaller.getInstalledFeatures();
	}

	@NotNull
	@Override
	public <C, R> R install(@NotNull Feature<C, R> feature, @NotNull UnaryOperator<C> configure) {
		if (isRegistered())
			throw new IllegalStateException("Cannot register a feature after framework registration");

		final R value = featureInstaller.install(feature, configure);
		getOwner()
			.getLogger()
			.info(String.format(
				"Feature %s installed",
				StringUtils.substringBeforeLast(feature.getClass().getSimpleName(), "Feature")));
		return value;
	}

	@Override
	public void uninstall(@NotNull Feature<?, ?> feature) {
		if (isRegistered())
			throw new IllegalStateException("Cannot unregister a feature after framework registration");

		featureInstaller.uninstall(feature);
		getOwner()
			.getLogger()
			.info(String.format(
				"Feature %s uninstalled",
				StringUtils.substringBeforeLast(feature.getClass().getSimpleName(), "Feature")));
	}

	@Override
	public void nextTick(@NotNull Runnable runnable) {
		getOwner().getServer().getScheduler().runTask(getOwner(), runnable);
	}

	@Override
	public BiConsumer<PaginatedViewContext<?>, ViewItem> getPreviousPageItem() {
		return previousPageItem;
	}

	@Override
	public Function<PaginatedViewContext<?>, ViewItem> getDefaultPreviousPageItem() {
		return (context) -> new ViewItem();
	}

	@Override
	public void setDefaultPreviousPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItemFactory) {
		this.previousPageItem = (context, item) -> defaultPreviousPageItemFactory.apply(context);
	}

	@Override
	public ViewFrame withPreviousPageItem(@Nullable BiConsumer<PaginatedViewContext<?>, ViewItem> previousPageItemFactory) {
		this.previousPageItem = previousPageItemFactory;
		return this;
	}

	@Override
	public ViewFrame setNavigateBackItemFactory(BiConsumer<PaginatedViewContext<?>, ViewItem> navigateBackItemFactory) {
		this.previousPageItem = navigateBackItemFactory;
		return this;
	}

	@Override
	public BiConsumer<PaginatedViewContext<?>, ViewItem> getNextPageItem() {
		return nextPageItem;
	}

	@Override
	public Function<PaginatedViewContext<?>, ViewItem> getDefaultNextPageItem() {
		return (context) -> new ViewItem();
	}

	@Override
	public void setDefaultNextPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItemFactory) {
		this.nextPageItem = (context, item) -> defaultNextPageItemFactory.apply(context);
	}

	@Override
	public ViewFrame withNextPageItem(@Nullable BiConsumer<PaginatedViewContext<?>, ViewItem> nextPageItemFactory) {
		this.nextPageItem = nextPageItemFactory;
		return this;
	}

	@Override
	public ViewFrame setNavigateNextItemFactory(BiConsumer<PaginatedViewContext<?>, ViewItem> navigateNextItemFactory) {
		this.nextPageItem = navigateNextItemFactory;
		return this;
	}

	@Override
	public ViewFrame withErrorHandler(@NotNull ViewErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}

	public static ViewFrame of(@NotNull Plugin plugin, @NotNull AbstractView... views) {
		return new ViewFrame(plugin).with(views);
	}
}
