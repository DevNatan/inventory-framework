package me.saiintbrisson.minecraft;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class DelegatedViewContext extends ViewSlotContext {

    private final ViewContext delegate;
    private boolean cancelled;

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
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Map<Integer, Map<String, Object>> slotData() {
        return delegate.slotData();
    }

    @Override
    public Map<String, Object> getSlotData() {
        return delegate.getSlotData(getSlot());
    }

    @Override
    public Map<String, Object> getSlotData(int slot) {
        return delegate.getSlotData(getSlot());
    }

    @Override
    public void update() {
        delegate.update();
    }

    @Override
    public <T> PaginatedViewContext<T> paginated() {
        return delegate.paginated();
    }

    @Override
    public String toString() {
        return "DelegatedViewContext{" +
                "cancelled=" + cancelled +
                ", delegate=" + delegate +
                "} " + super.toString();
    }

}