package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.BukkitComponent;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.utils.SlotConverter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ComponentRenderContext extends ComponentContext implements IFComponentRenderContext {

    private final Viewer viewer;
    private final Player player;
    private ItemStack item;
    private int slot;

    public ComponentRenderContext(RenderContext parent, BukkitComponent component, Viewer viewer) {
        super(parent, component);
        this.viewer = viewer;
        this.player = viewer == null ? null : ((BukkitViewer) viewer).getPlayer();
        this.item = component.getItem();
        this.slot = component.getPosition();
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

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setSlot(int row, int column) {
        setSlot(SlotConverter.convertSlot(
                row, column, getContainer().getRowsCount(), getContainer().getColumnsCount()));
    }

    @Override
    public String toString() {
        return "ComponentRenderContext{" + "viewer=" + viewer + ", player=" + player + ", item=" + item + "} "
                + super.toString();
    }
}
