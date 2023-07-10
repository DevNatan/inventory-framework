package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.ApiStatus;

public interface ItemComponentBuilder<S extends ItemComponentBuilder<S>> extends ComponentBuilder<S> {

    /**
     * Sets the slot that the item will be positioned.
     *
     * @param slot The item slot.
     * @return This item builder.
     */
    S withSlot(int slot);

    // TODO needs documentation
    @ApiStatus.Experimental
    S withSlot(int row, int column);

    @ApiStatus.Internal
    boolean isContainedWithin(int position);
}
