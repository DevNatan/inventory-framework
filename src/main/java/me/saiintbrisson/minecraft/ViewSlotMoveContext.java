package me.saiintbrisson.minecraft;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ViewSlotMoveContext extends DelegatedViewContext {

    private final Inventory targetInventory;
    private final ItemStack targetItem;
    private final int targetSlot;
    private final boolean swap;

    public ViewSlotMoveContext(final ViewContext delegate, final int slot, final ItemStack item, final Inventory targetInventory, final ItemStack targetItem, final int targetSlot, final boolean swap) {
        super(delegate, slot, item);
        this.targetInventory = targetInventory;
        this.targetItem = targetItem;
        this.targetSlot = targetSlot;
        this.swap = swap;
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

    public ItemStack getTargetItem() {
        return targetItem;
    }

    public ItemStack getSwappedItem() {
        if (!isSwap())
            throw new IllegalStateException("Not a swap");

        return getTargetItem();
    }

    public int getTargetSlot() {
        return targetSlot;
    }

    public boolean isSwap() {
        return swap;
    }

}