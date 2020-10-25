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
    private boolean selfRegister = true;
    private Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItem;
    private Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItem;

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

    public boolean isSelfRegister() {
        return selfRegister;
    }

    public void setSelfRegister(boolean selfRegister) {
        this.selfRegister = selfRegister;
    }

    public Map<Class<? extends View>, View> getRegisteredViews() {
        return registeredViews;
    }

    public final void addView(View view) {
        if (view.getFrame() == null)
            view.setFrame(this);
        registeredViews.put(view.getClass(), view);
    }

    public void addView(View... views) {
        for (View view : views) {
            addView(view);
        }
    }

    public void register() {
        checkUnregistered();
        this.listener = new ViewListener(this);
        Bukkit.getPluginManager().registerEvents(listener, owner);
    }

    public void unregister() {
        Iterator<View> iterator = registeredViews.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
            iterator.remove();
        }

        if (listener != null)
            HandlerList.unregisterAll(listener);
        listener = null;
    }

    public <T extends View> T open(Class<T> view, Player player) {
        return open(view, player, null);
    }

    public <T extends View> T open(Class<T> view, Player player, Map<String, Object> data) {
        T openedView = createView(view);
        if (openedView == null)
            throw new IllegalArgumentException("View " + view.getSimpleName() + " is not registered");

        openedView.open(player, data);
        return openedView;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T createView(Class<T> view) {
        return (T) getRegisteredViews().get(view);
    }

    private void checkUnregistered() {
        if (listener != null)
            throw new IllegalStateException("Listener already registered.");
    }

    public Function<PaginatedViewContext<?>, ViewItem> getDefaultPreviousPageItem() {
        return defaultPreviousPageItem;
    }

    public Function<PaginatedViewContext<?>, ViewItem> getDefaultNextPageItem() {
        return defaultNextPageItem;
    }

    public void setDefaultPreviousPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItem) {
        this.defaultPreviousPageItem = defaultPreviousPageItem;
    }

    public void setDefaultNextPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItem) {
        this.defaultNextPageItem = defaultNextPageItem;
    }

}
