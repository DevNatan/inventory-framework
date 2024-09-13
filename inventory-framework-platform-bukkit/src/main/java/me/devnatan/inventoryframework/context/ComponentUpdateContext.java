package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import org.bukkit.entity.Player;

public final class ComponentUpdateContext extends ComponentContext implements IFComponentUpdateContext {

    private final Viewer viewer;
    private final Player player;
    private boolean forceUpdate, cancelled;

    public ComponentUpdateContext(RenderContext parent, Component component, Viewer viewer) {
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
        return getParent().getContainer();
    }

    @Override
    public boolean isForceUpdate() {
        return forceUpdate;
    }

    @Override
    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
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
        return "ComponentUpdateContext{" + "viewer="
                + viewer + ", player="
                + player + ", forceUpdate="
                + forceUpdate + ", cancelled="
                + cancelled + "} "
                + super.toString();
    }
}
