package me.saiintbrisson.minecraft;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class DelegatedViewContext extends ViewSlotContext {

    private final ViewContext delegate;

    public DelegatedViewContext(ViewContext delegate, int slot, ItemStack item) {
        super(delegate.getView(), delegate.getPlayer(), delegate.getInventory(), slot, item);
        this.delegate = delegate;
    }

    @Override
    public ViewItem[] getItems() {
        return delegate.getItems();
    }

    @Override
    public ViewItem getItem(int slot) {
        return delegate.getItem(slot);
    }

    @Override
    public Map<Integer, Map<String, Object>> slotData() {
        return delegate.slotData();
    }

    @Override
    public void update() {
        delegate.update();
    }

    @Override
    public String toString() {
        return "DelegatedViewContext{" +
                "delegate=" + delegate +
                "} " + super.toString();
    }

}