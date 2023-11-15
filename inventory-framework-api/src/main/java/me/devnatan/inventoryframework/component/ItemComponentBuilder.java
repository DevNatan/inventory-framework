package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.ApiStatus;

public interface ItemComponentBuilder<SELF, ITEM> extends ComponentBuilder<SELF> {

    /**
     * Sets the slot that the item will be positioned.
     *
     * @param slot The item slot.
     * @return This component builder.
     */
    SELF withSlot(int slot);

    /**
     * Sets the row and column that the item will be positioned.
     *
     * @param row The row (X)
     * @param column The column (Y)
     * @return This component builder.
     */
    SELF withSlot(int row, int column);

	/**
	 * Sets the item that will be rendered in where this component is placed on.
	 * The rendered item is always static. For dynamic rendering use
	 *
	 * @param item The item.
	 * @return This component builder.
	 */
	SELF withItem(ITEM item);

    @ApiStatus.Internal
    boolean isContainedWithin(int position);
}
