package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.*;

public final class ViewFrame {

    private final Plugin owner;
    private Listener listener;
    private final Map<Class<? extends View>, View> registeredViews;
    private boolean selfRegister = true;

    public ViewFrame(Plugin owner) {
        this.owner = owner;
        registeredViews = new HashMap<>();
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

    public <T extends View> ViewFrame addView(T... views) {
        for (T view : views) {
            if (view.getFrame() == null)
                view.setFrame(this);

            owner.getLogger().info("View " + view.getClass().getSimpleName() + " registered.");
            registeredViews.put(view.getClass(), view);
        }

        owner.getLogger().info("Registered " + registeredViews.size() + " views.");
        return this;
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

}
