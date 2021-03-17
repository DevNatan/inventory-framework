package me.saiintbrisson.minecraft;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ViewSlotMoveContext extends DelegatedViewContext {

    private final Inventory targetInventory;
    private final ItemStack swappedItem;
    private final int targetSlot;

    public ViewSlotMoveContext(ViewContext delegate, int slot, ItemStack item, Inventory targetInventory, ItemStack swappedItem, int targetSlot) {
        super(delegate, slot, item);
        this.targetInventory = targetInventory;
        this.swappedItem = swappedItem;
        this.targetSlot = targetSlot;
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        delegate.setCancelled(cancelled);
    }

    public Inventory getTargetInventory() {
        return targetInventory;
    }

    public ItemStack getSwappedItem() {
        return swappedItem;
    }

    public boolean isSwap() {
        return swappedItem != null;
    }

    public int getTargetSlot() {
        return targetSlot;
    }

}