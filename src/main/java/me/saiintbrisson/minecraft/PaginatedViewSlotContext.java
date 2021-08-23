package me.saiintbrisson.minecraft;

public class PaginatedViewSlotContext<T> extends PaginatedViewContext<T> {

    private final int index;
    private final int slot;

    public PaginatedViewSlotContext(PaginatedViewContext<T> delegate, int index, int slot) {
        super(delegate.getView(), delegate.getPlayer(), delegate.getInventory(), delegate.getPage());
        this.index = index;
        this.slot = slot;
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

}