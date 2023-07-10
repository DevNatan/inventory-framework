package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.component.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a context in which there is a specific slot related to it, the main context
 * encompasses the entire container in an actor's view, the ViewSlotContext encapsulates a context
 * for just one slot of a container.
 *
 * <p>Methods specific to a ViewSlotContext will only apply to that slot.
 *
 * @see IFContext
 * @see IFSlotClickContext
 */
public interface IFSlotContext extends IFContext {

    /**
     * The parent context of this context.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @return The parent context of this context.
     */
    @ApiStatus.Internal
    IFContext getParent();

    /**
     * Clears this slot from the current context.
     * <p>
     * The slot will only be cleaned on the next update, so if you want it cleaned immediately
     * update the slot using {@link #updateSlot()}.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    void clear();

    /**
     * Returns the slot position of this context in the current container.
     *
     * @return The slot position of this context.
     */
    int getSlot();

    // TODO needs documentation about dynamic slot positioning (some cases are unsupported)
    void setSlot(int slot);

    /**
     * Updates this slot.
     */
    void updateSlot();

    /**
     * Returns the wrapper containing the item related to this context.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @return The current item wrapper.
     */
    //    @ApiStatus.Internal
    //    @NotNull
    //    ItemWrapper getItemWrapper();

    /**
     * Returns the current item of this context.
     * <p>
     * The item returned is not necessarily the item positioned in the slot, there are cases, for
     * example in {@link IFSlotMoveContext}, in which the current item may be the item the entity
     * is interacting with and not a positioned item.
     *
     * @return The current item.
     */
    //    @NotNull
    //    ItemWrapper getCurrentItem();

    /**
     * Sets the new item for this slot for this context.
     *
     * @param item The new item that'll be set.
     * @throws InventoryModificationException When the container is changed.
     */
    //    void setItem(@Nullable Object item) throws InventoryModificationException;

    /**
     * Whether this context originated from an interaction coming from the actor's container and not
     * from the view's container.
     *
     * @return If this context originated from the actor's container
     */
    boolean isOnEntityContainer();

    /**
     * Checks if the item in this context has been changed.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @return If the item in this context has been changed.
     */
    @ApiStatus.Internal
    boolean hasChanged();

    /**
     * Marks this context as changed.
     *
     * <p>Improperly changing this property can cause unexpected side effects.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param changed If the context item was changed.
     */
    @ApiStatus.Internal
    void setChanged(boolean changed);

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    boolean isRegistered();

    // TODO documentation
    // can be null if context origin is outside root view container
    @Nullable
    Component getComponent();
}
