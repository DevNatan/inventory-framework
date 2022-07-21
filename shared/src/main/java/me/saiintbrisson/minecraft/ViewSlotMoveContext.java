package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

/**
 * A movement context is created when there is a need to describe an action in which the item was
 * moved from one place to another, either within the same container or between containers, in which
 * there is information about the item in which it was interacted if there is one.
 *
 * <p>The properties of the current context are the source definitions and the properties named with
 * "getTarget..." are the targets.
 *
 * @see ViewSlotContext
 * @see ViewSlotClickContext
 */
public interface ViewSlotMoveContext extends ViewSlotClickContext {

    /**
     * The container the target item is in.
     *
     * @return The container the target item is in.
     */
    @NotNull
    ViewContainer getTargetContainer();

    /**
     * The position of the item that was interacted with in the target container.
     *
     * @return The position of the item that was interacted with.
     */
    int getTargetSlot();

    /**
     * A wrapper containing the target item that was interacted with.
     *
     * @return The item that was interacted with.
     */
    @NotNull
    ItemWrapper getTargetItem();

    /**
     * A wrapper with the item that was replaced if there is a swap.
     *
     * @return The item that was swapped.
     * @throws IllegalStateException If the items have not been swapped.
     * @see #isSwap()
     */
    @NotNull
    ItemWrapper getSwappedItem();

    /**
     * Whether the source item and the target item were replaced.
     *
     * <p>When this happens the target item goes to the sprite's cursor and the source item takes th e
     * place of the target item.
     *
     * @return Whether the source item and the target item were replaced.
     */
    boolean isSwap();

    /**
     * Whether the source item stacked with the target item. For this happen both items must be
     * similar. You can get the stacking result by accessing the target container.
     *
     * @return Whether the source item stacked with the target item.
     */
    boolean isStack();
}
