package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import org.bukkit.entity.Player;

public final class ComponentClearContext extends ComponentContext implements IFComponentClearContext {

    private final Viewer viewer;
    private final Player player;
    private boolean cancelled;

    public ComponentClearContext(RenderContext parent, Component component, Viewer viewer) {
        super(parent, component);
        this.viewer = viewer;
        this.player = viewer == null ? null : ((BukkitViewer) viewer).getPlayer();
    }

    @Override
    public Viewer getViewer() {
        return viewer;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public ViewContainer getContainer() {
        return getComponent().getContainer();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "ComponentClearContext{" + "viewer=" + viewer + ", player=" + player + ", cancelled=" + cancelled + "} "
                + super.toString();
    }
}
