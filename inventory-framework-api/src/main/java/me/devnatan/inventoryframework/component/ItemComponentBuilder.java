package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.jetbrains.annotations.ApiStatus;

/**
 * A component builder for the {@link ItemComponent} component.
 *
 * @param <SELF> The self reference of this component builder.
 * @param <ITEM> Item value type of the current platform.
 */
public interface ItemComponentBuilder<SELF extends ComponentBuilder<SELF>, ITEM> extends ComponentBuilder<SELF> {

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
     *
     * @param item The item.
     * @return This component builder.
     */
    SELF withItem(ITEM item);

    @ApiStatus.Internal
    boolean isContainedWithin(int position);

	@Override
	ItemComponent build(VirtualView root);
}
