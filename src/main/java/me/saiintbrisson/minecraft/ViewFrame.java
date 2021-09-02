package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public final class ViewFrame {

	private final Plugin owner;
	private Listener listener;
	private final Map<Class<? extends View>, View> registeredViews;
	private Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItem;
	private Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItem;
	private boolean debugEnabled;

	public ViewFrame(Plugin owner) {
		this.owner = owner;
		registeredViews = new HashMap<>();
	}

	public ViewFrame(Plugin owner, View... views) {
		this(owner);
		addView(views);
	}

	public Plugin getOwner() {
		return owner;
	}

	public Listener getListener() {
		return listener;
	}

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
		for (final View view : views) {
			addView(view);
		}
	}

	public void register(final View... views) {
		checkUnregistered();

		for (final View view : views)
			addView(view);

		this.listener = new ViewListener(this);
		Bukkit.getPluginManager().registerEvents(listener, owner);
		debug("[frame] registered to " + owner.getName());
	}

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
		final T openedView = getView(view);
		if (openedView == null)
			throw new IllegalArgumentException("View " + view.getSimpleName() + " is not registered");

		openedView.open(player, data);
		return openedView;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getView(Class<T> view) {
		return (T) getRegisteredViews().get(view);
	}

	private void checkUnregistered() {
		if (listener != null)
			throw new IllegalStateException("Listener already registered.");
	}

	public Function<PaginatedViewContext<?>, ViewItem> getDefaultPreviousPageItem() {
		return defaultPreviousPageItem;
	}

	public void setDefaultPreviousPageItem(final Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItem) {
		this.defaultPreviousPageItem = defaultPreviousPageItem;
	}

	public Function<PaginatedViewContext<?>, ViewItem> getDefaultNextPageItem() {
		return defaultNextPageItem;
	}

	public void setDefaultNextPageItem(final Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItem) {
		this.defaultNextPageItem = defaultNextPageItem;
	}

	public void debug(String message) {
		if (!debugEnabled)
			return;

		getOwner().getLogger().info("[IF DEBUG] " + message);
	}

}
