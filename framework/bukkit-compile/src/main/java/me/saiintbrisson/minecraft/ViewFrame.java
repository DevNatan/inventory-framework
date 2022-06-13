package me.saiintbrisson.minecraft;

import lombok.*;
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
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public final class ViewFrame implements CompatViewFrame<ViewFrame> {

	@NotNull
	private final Plugin owner;

	private ViewErrorHandler errorHandler;

	@ToString.Exclude
	private final Map<Class<? extends AbstractView>, View> views = new HashMap<>();

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

	@Override
	public AbstractView get(@NotNull Player player) {
		final Inventory inventory = player.getOpenInventory().getTopInventory();

		// fast path -- check for inventory type first
		if (inventory.getType() == InventoryType.PLAYER)
			return null;

		// TODO: search for alternative ways to retrieve current view because
		//       InventoryHolder is deprecated in newer versions of Bukkit API
		final InventoryHolder holder = inventory.getHolder();
		if (!(holder instanceof View))
			return null;

		final View view = (View) holder;
		if (inventory.getType() != InventoryType.CHEST)
			throw new UnsupportedOperationException("Views is only supported on chest-type inventory.");

		return view;
	}

	/**
	 * @deprecated Will be removed soon.
	 */
	@Deprecated
	public Map<Class<? extends View>, View> getRegisteredViews() {
		return Collections.unmodifiableMap(legacyViews);
	}

	@Override
	public ViewFrame with(@NotNull View... views) {
		synchronized (getViews()) {
			for (final View view : views) {
				if (getViews().containsKey(view.getClass()))
					throw new IllegalArgumentException(String.format(
						"View %s already registered, try to use #with before #register instead.",
						view.getClass().getName()
					));

				getViews().put(view.getClass(), view);
				legacyViews.put(view.getClass(), view);
			}
		}
		return this;
	}

	@Override
	public void remove(@NotNull View... views) {
		synchronized (getViews()) {
			for (final View view : views) {
				view.close();
				getViews().remove(view.getClass());
			}
		}
	}

	/**
	 * @deprecated Use {@link #register()} and {@link #with(View...)} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.2")
	public void register(@NotNull View... views) {
		with(views);
		register();
	}

	@Override
	public void register() {
		if (isRegistered())
			throw new IllegalStateException("This ViewFrame is already registered.");

		// check if there's another ViewFrame instance in the same server
		final ServicesManager servicesManager = getOwner().getServer().getServicesManager();
		if (servicesManager.isProvidedFor(ViewFrame.class))
			return;

		for (final View view : views.values()) {
			view.setViewFrame(this);
			PlatformUtils.getFactory().setupView(view);
		}

		final Plugin plugin = getOwner();
		plugin.getServer().getPluginManager().registerEvents(
			listener = new ViewListener(plugin, this),
			plugin
		);
		servicesManager.register(ViewFrame.class, this, plugin, ServicePriority.Normal);
	}

	@Override
	public void unregister() {
		if (!isRegistered())
			throw new IllegalStateException("Not registered");

		final Iterator<View> viewIterator = views.values().iterator();
		while (viewIterator.hasNext()) {
			final View view = viewIterator.next();
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

		view.open(PlatformUtils.getFactory().createViewer(player), data);
		return view;
	}

	/**
	 * @deprecated Use {@link #with(View...)} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.2")
	public void addView(final View view) {
		with(view);
	}

	/**
	 * @deprecated Use {@link #with(View...)} instead.
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
		return featureInstaller.install(feature, configure);
	}

	public static ViewFrame of(
		@NotNull Plugin plugin,
		@NotNull View... views
	) {
		return new ViewFrame(plugin).with(views);
	}

}
