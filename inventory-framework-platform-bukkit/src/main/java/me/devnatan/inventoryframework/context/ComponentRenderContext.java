package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ComponentRenderContext extends ComponentContext implements IFComponentRenderContext {

    private final Viewer viewer;
    private final Player player;
    private ItemStack item;

    public ComponentRenderContext(RenderContext parent, Component component, Viewer viewer) {
        super(parent, component);
        this.viewer = viewer;
        this.player = viewer == null ? null : ((BukkitViewer) viewer).getPlayer();
        this.item =
                component instanceof ItemComponent ? (ItemStack) ((ItemComponent) component).getPlatformItem() : null;
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

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "ComponentRenderContext{" + "viewer=" + viewer + ", player=" + player + ", item=" + item + "} "
                + super.toString();
    }
}
