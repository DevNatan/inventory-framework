package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.ApiStatus;

public interface ItemComponentBuilder<Self extends ItemComponentBuilder<Self, Context>, Context extends IFContext>
        extends ComponentBuilder<Self, Context> {

    /**
     * Sets the slot that the item will be positioned.
     *
     * @param slot The item slot.
     * @return This item builder.
     */
    Self withSlot(int slot);

    // TODO needs documentation
    @ApiStatus.Experimental
    Self withSlot(int row, int column);

    @ApiStatus.Internal
    boolean isContainedWithin(int position);
}
