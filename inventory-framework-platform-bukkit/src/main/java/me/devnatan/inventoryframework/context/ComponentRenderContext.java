package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import org.bukkit.entity.Player;

public final class ComponentRenderContext extends ComponentContext implements IFComponentRenderContext {

    private final Viewer viewer;
    private final Player player;

    public ComponentRenderContext(RenderContext parent, Component component, Viewer viewer) {
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
    public String toString() {
        return "ComponentRenderContext{" + "viewer=" + viewer + ", player=" + player + "} " + super.toString();
    }
}
