package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public final class ViewFrame {

	private final Plugin owner;
	private final Map<Class<? extends View>, View> registeredViews;
	private Listener listener;
	private Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItem;
	private Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItem;
	private boolean debugEnabled;
	private ViewErrorHandler globalErrorHandler;

	public ViewFrame(@NotNull Plugin owner) {
		this.owner = owner;
		registeredViews = new HashMap<>();
	}

	/**
	 * @deprecated Use the main constructor and then register your views.
	 */
	@Deprecated
	public ViewFrame(Plugin owner, View... views) {
		this(owner);
		addView(views);
	}

	public Plugin getOwner() {
		return owner;
	}

	/**
	 * @deprecated This is an internal API and will be removed soon.
	 */
	@ApiStatus.Internal
	public Listener getListener() {
		return listener;
	}

	/**
	 * @deprecated This is an internal API and will be removed soon.
	 */
	@ApiStatus.Internal
	public Map<Class<? extends View>, View> getRegisteredViews() {
		return registeredViews;
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

	public final void addView(final View view) {
		if (view.getFrame() == null)
			view.setFrame(this);

		registeredViews.put(view.getClass(), view);
		debug("[view] \"" + view.getClass().getSimpleName() + "\" registered.");
	}

	public void addView(final View... views) {
		tryRegister(views);
	}

	private void tryRegister(final View... views) {
		for (final View view : views) {
			if (registeredViews.containsKey(view.getClass()))
				throw new IllegalArgumentException("View " + view.getClass().getName() + " already registered, try to" +
					" use `addView` before `register`.");
			addView(view);
		}
	}

	public void register(final View... views) {
		checkUnregistered();
		tryRegister(views);

		this.listener = new ViewListener(this);

		if (ensureProvider()) {
			Bukkit.getPluginManager().registerEvents(listener, owner);
			debug("[frame] registered to " + owner.getName());
		} else {
			debug("[frame] provider already registered, we will not register again the View Listener. ");
		}
	}

	/**
	 * @deprecated Will become private soon.
	 */
	@Deprecated
	public void unregister() {
		Iterator<View> iterator = registeredViews.values().iterator();
		while (iterator.hasNext()) {
			final View view = iterator.next();
			view.close();
			iterator.remove();
			debug("[view] \"" + view.getClass().getSimpleName() + "\" unregistered.");
		}

		if (listener != null) {
			HandlerList.unregisterAll(listener);
			listener = null;
			debug("[frame] unregistered from " + owner.getName());
		}
	}

	public <T extends View> T open(Class<T> view, Player player) {
		return open(view, player, null);
	}

	public <T extends View> T open(Class<T> view, Player player, Map<String, Object> data) {
		if (listener == null)
			throw new IllegalStateException("Attempt to open a View without having registered the ViewFrame");

		final T openedView = getView(view);
		if (openedView == null)
			throw new IllegalArgumentException("View " + view.getSimpleName() + " is not registered");

		openedView.open(player, data);
		return openedView;
	}

	/**
	 * @deprecated Will become private soon.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(Class<T> view) {
		return (T) getRegisteredViews().get(view);
	}

	private void checkUnregistered() {
		if (listener != null)
			throw new IllegalStateException("Listener already registered.");
	}

	/**
	 * @deprecated Will become internal soon.
	 */
	@Deprecated
	public Function<PaginatedViewContext<?>, ViewItem> getDefaultPreviousPageItem() {
		return defaultPreviousPageItem;
	}

	public void setDefaultPreviousPageItem(final Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItem) {
		this.defaultPreviousPageItem = defaultPreviousPageItem;
	}

	/**
	 * @deprecated Will become internal soon.
	 */
	@Deprecated
	public Function<PaginatedViewContext<?>, ViewItem> getDefaultNextPageItem() {
		return defaultNextPageItem;
	}

	public void setDefaultNextPageItem(final Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItem) {
		this.defaultNextPageItem = defaultNextPageItem;
	}

	/**
	 * @deprecated Will become internal soon.
	 */
	@Deprecated
	public void debug(String message) {
		if (!debugEnabled)
			return;

		getOwner().getLogger().info("[IF DEBUG] " + message);
	}

	/**
	 * Gets the global error handler for all views.
	 *
	 * @return The global error handler.
	 */
	public ViewErrorHandler getErrorHandler() {
		return globalErrorHandler;
	}

	/**
	 * Sets the global error handler for all views.
	 *
	 * @param errorHandler The global error handler. Use <code>null</code> to
	 *                     remove if it's already defined.
	 */
	public void setErrorHandler(@Nullable ViewErrorHandler errorHandler) {
		this.globalErrorHandler = errorHandler;
	}

	private boolean ensureProvider() {
		ServicesManager servicesManager = Bukkit.getServicesManager();

		if (!servicesManager.isProvidedFor(ViewProvider.class)) {
			servicesManager.register(ViewProvider.class, new DelegatedViewProvider(this), owner, ServicePriority.Normal);

			return true;
		}

		return false;
	}

}
