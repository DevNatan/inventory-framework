package me.saiintbrisson.minecraft;

import lombok.*;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Getter
@Setter
@ToString
public final class ViewFrame implements CompatViewFrame<ViewFrame> {

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
	private Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItem, defaultNextPageItem;

	@Getter(AccessLevel.NONE)
	private final FeatureInstaller<ViewFrame> featureInstaller
		= new DefaultFeatureInstaller<>(this);

	static {
		PlatformUtils.setFactory(new BukkitViewComponentFactory());
	}

	/**
	 * Creates a new ViewFrame instance.
	 *
	 * @param owner The Bukkit plugin holder of this view framework.
	 * @deprecated Use {@link #of(Plugin, View...)} instead.
	 */
	@Deprecated
	public ViewFrame(@NotNull Plugin owner) {
		this.owner = owner;
	}

	@Override
	public AbstractView get(@NotNull Player player) {
		final Inventory inventory = player.getOpenInventory().getTopInventory();

		// fast path -- check for inventory type first
		if (inventory.getType() == InventoryType.PLAYER)
			return null;

		// TODO: search for alternative ways to retrieve current view because
		//       InventoryHolder is deprecated in newer versions of Bukkit API
		final InventoryHolder holder = inventory.getHolder();
		if (!(holder instanceof AbstractView))
			return null;

		return (AbstractView) holder;
	}

	/**
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
						view.getClass().getName()
					));

				getViews().put(view.getClass(), view);

				if (view instanceof View)
					legacyViews.put(((View) view).getClass(), (View) view);
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
	 * @deprecated Use {@link #register()} and {@link #with(AbstractView...)} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.2")
	public void register(@NotNull View... views) {
		with(views);
		register();
	}

	@Override
	public ViewFrame register() {
		if (isRegistered())
			throw new IllegalStateException("This ViewFrame is already registered.");

		// check if there's another ViewFrame instance in the same server
		final ServicesManager servicesManager = getOwner().getServer().getServicesManager();
		if (servicesManager.isProvidedFor(ViewFrame.class))
			return this;

		for (final AbstractView view : views.values()) {
			view.setViewFrame(this);
			view.setInitialized(true);
			PlatformUtils.getFactory().setupView(view);
			owner.getLogger().info("\"" + view.getClass().getSimpleName() + "\" registered");
		}

		final Plugin plugin = getOwner();
		plugin.getServer().getPluginManager().registerEvents(
			listener = new ViewListener(plugin, this),
			plugin
		);
		servicesManager.register(ViewFrame.class, this, plugin, ServicePriority.Normal);
		return this;
	}

	@Override
	public void unregister() {
		if (!isRegistered())
			throw new IllegalStateException("Not registered");

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
	public <T extends AbstractView> T open(
		@NotNull Class<T> viewClass,
		@NotNull Player player
	) {
		return open(viewClass, player, Collections.emptyMap());
	}

	@Override
	public <T extends AbstractView> T open(
		@NotNull Class<T> viewClass,
		@NotNull Player player,
		@NotNull Map<String, Object> data
	) {
		if (!isRegistered())
			throw new IllegalStateException("Attempt to open a view without having registered the view frame.");

		@SuppressWarnings("unchecked") final T view = (T) views.get(viewClass);

		if (view == null)
			throw new IllegalStateException(String.format(
				"View %s is not registered.",
				viewClass.getName()
			));

		getOwner().getServer().getScheduler().runTaskLater(getOwner(),
			() -> view.open(PlatformUtils.getFactory().createViewer(player), data), 1L);
		return view;
	}

	/**
	 * @deprecated Use {@link #with(AbstractView...)} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.2")
	public void addView(final View view) {
		with(view);
	}

	/**
	 * @deprecated Use {@link #with(AbstractView...)} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.2")
	public void addView(final View... views) {
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
		getOwner().getLogger().info(String.format(
			"Feature %s installed",
			StringUtils.substringBeforeLast(feature.getClass().getSimpleName(), "Feature")
		));
		return value;
	}

	@Override
	public void nextTick(Runnable runnable) {
		getOwner().getServer().getScheduler().runTask(getOwner(), runnable);
	}

	@Override
	public ViewFrame setNavigateBackItemFactory(BiConsumer<PaginatedViewContext<?>, ViewItem> navigateBackItemFactory) {
		defaultPreviousPageItem = (context) -> {
			final ViewItem item = new ViewItem();
			navigateBackItemFactory.accept(context, item);
			return item;
		};
		return this;
	}

	@Override
	public ViewFrame setNavigateNextItemFactory(BiConsumer<PaginatedViewContext<?>, ViewItem> navigateNextItemFactory) {
		defaultNextPageItem = (context) -> {
			final ViewItem item = new ViewItem();
			navigateNextItemFactory.accept(context, item);
			return item;
		};
		return this;
	}

	public static ViewFrame of(
		@NotNull Plugin plugin,
		@NotNull View... views
	) {
		return new ViewFrame(plugin).with(views);
	}

}
