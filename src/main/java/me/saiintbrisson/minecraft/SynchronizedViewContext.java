package me.saiintbrisson.minecraft;

import org.bukkit.inventory.ItemStack;

public class SynchronizedViewContext extends ViewSlotContext {

    private final ViewContext delegate;

    public SynchronizedViewContext(ViewContext delegate, int slot, ItemStack item) {
        super(delegate.getView(), delegate.getPlayer(), delegate.getInventory(), slot, item);
        this.delegate = delegate;
    }

    @Override
    public ViewItem[] getItems() {
        return delegate.getItems();
    }
}