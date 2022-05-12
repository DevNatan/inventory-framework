package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@ToString
public final class ViewFrame {

	private final Plugin plugin;
	private ViewErrorHandler errorHandler;

	@ToString.Exclude
	private final Map<Class<? extends View>, View> views = new HashMap<>();
	private final ViewContainerFactory containerFactory = new BukkitViewContainerFactory();

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Listener listener;
	private Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItem, defaultNextPageItem;

	/**
	 * @deprecated Use {@link ViewFrame#of(Plugin, View...)} instead.
	 */
	@Deprecated
	public ViewFrame(@NotNull Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Registers a new view to this view frame.
	 *
	 * @param views The views that'll be registered.
	 * @return This view frame instance.
	 */
	public ViewFrame with(@NotNull View... views) {
		synchronized (getViews()) {
			for (final View view : views) {
				getViews().put(view.getClass(), view);
			}
		}
		return this;
	}

	/**
	 * Removes a view from this view frame.
	 *
	 * @param views The views that'll be removed.
	 */
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
	public void register(@NotNull View... views) {
		with(views);
		register();
	}

	/**
	 * Registers this view frame.
	 *
	 * @throws IllegalStateException If this view frame is already registered.
	 */
	public void register() {
		if (isRegistered())
			throw new IllegalStateException("Already registered");

		synchronized (views) {
			for (final View view : views.values()) {
				if (views.containsKey(view.getClass()))
					throw new IllegalArgumentException(
						"View already registered, try to use #with before #register instead."
					);

				views.put(view.getClass(), view);
			}
		}

		// check if there's another ViewFrame instance in the same server
		final ServicesManager servicesManager = plugin.getServer().getServicesManager();

		if (!servicesManager.isProvidedFor(ViewFrame.class)) {
			servicesManager.register(ViewFrame.class, this, plugin, ServicePriority.Normal);
			return;
		}

		plugin.getServer().getPluginManager().registerEvents(
			listener = new ViewListener(this),
			plugin
		);
	}

	/**
	 * Unregisters this view frame and closes all registered views.
	 *
	 * @throws IllegalStateException If this view frame is not registered.
	 */
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

	/**
	 * Returns `true` if this view frame is registered or `false` otherwise.
	 *
	 * @return If this view frame is registered.
	 */
	public boolean isRegistered() {
		return listener != null;
	}

	public <T extends View> T open(
		@NotNull Class<T> viewClass,
		@NotNull Player player
	) {
		return open(viewClass, player, new HashMap<>());
	}

	public <T extends View> T open(
		@NotNull Class<T> viewClass,
		@NotNull Player player,
		@NotNull Map<String, Object> data
	) {
		if (!isRegistered())
			throw new IllegalStateException("Attempted to open a view without having registered the view frame");

		@SuppressWarnings("unchecked") final T view = (T) views.get(viewClass);

		if (view == null)
			throw new IllegalStateException(String.format(
				"View %s is not registered",
				viewClass.getName()
			));

		view.open(player, data);
		return view;
	}

	/**
	 * @deprecated Use {@link #with(View...)} instead.
	 */
	@Deprecated
	public void addView(final View view) {
		with(view);
	}

	/**
	 * @deprecated Use {@link #with(View...)} instead.
	 */
	@Deprecated
	public void addView(final View... views) {
		with(views);
	}

	/**
	 * @deprecated Use {@link #getPlugin()} instead.
	 */
	@Deprecated
	public Plugin getOwner() {
		return getPlugin();
	}

	public static ViewFrame of(
		@NotNull Plugin plugin,
		@NotNull View... views
	) {
		return new ViewFrame(plugin).with(views);
	}

}
