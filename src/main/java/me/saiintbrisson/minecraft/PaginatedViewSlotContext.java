package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;

import java.util.List;
import java.util.Stack;

public class PaginatedViewSlotContext<T> extends PaginatedViewContext<T> {

    private final PaginatedViewContext<T> delegate;
    private final int index;
    private final int slot;

    public PaginatedViewSlotContext(PaginatedViewContext<T> delegate, int index, int slot) {
        super(delegate.getView(), delegate.getPlayer(), delegate.getInventory(), delegate.getPage());
        this.delegate = delegate;
        this.index = index;
        this.slot = slot;
    }

	@Override
	protected Stack<Integer> getItemsLayer() {
		return delegate.getItemsLayer();
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
	public List<T> getCurrentSource() {
		return delegate.getCurrentSource();
	}

	@Override
	Paginator<T> getPaginator() {
		return delegate.getPaginator();
	}

	@Override
	protected void setSource0(Object source) {
		throw new IllegalStateException("");
	}

	/**
     * Returns the item pagination index.
     *
     * @return the item pagination.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the item slot.
     *
     * @return the item slot.
     */
    public int getSlot() {
        return slot;
    }

    @Override
    public ViewItem[] getItems() {
        return delegate.getItems();
    }

    @Override
    public String toString() {
        return "PaginatedViewSlotContext{" +
                "index=" + index +
                ", slot=" + slot +
                "} " + super.toString();
    }

}