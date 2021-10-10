package me.saiintbrisson.minecraft;

import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Stack;

public class DelegatedViewContext extends ViewSlotContext {

    protected final ViewContext delegate;
    private boolean cancelled;

    public DelegatedViewContext(ViewContext delegate, int slot, ItemStack item) {
        super(delegate.getView(), delegate.getPlayer(), delegate.getInventory(), slot, item);
        this.delegate = delegate;
    }

    ViewContext getDelegate() {
        return delegate;
    }

	@Override
	protected Stack<Integer> getItemsLayer() {
		return delegate.getItemsLayer();
	}

	@Override
    public String[] getLayout() {
        return delegate.getLayout();
    }

    @Override
    boolean isCheckedLayerSignature() {
        return delegate.isCheckedLayerSignature();
    }

    @Override
    void setCheckedLayerSignature(boolean checkedLayerSignature) {
        delegate.setCheckedLayerSignature(checkedLayerSignature);
    }

    @Override
    public boolean isMarkedToClose() {
        return delegate.isMarkedToClose();
    }

    @Override
    public ViewItem[] getItems() {
        return delegate.getItems();
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
    public void close() {
        delegate.close();
    }

    @Override
    public void closeNow() {
        delegate.closeNow();
    }

    @Override
    public void cancelAndClose() {
        delegate.cancelAndClose();
    }

    @Override
    public void clear(int slot) {
        delegate.clear(slot);
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