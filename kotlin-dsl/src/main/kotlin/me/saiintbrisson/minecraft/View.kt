@file:JvmSynthetic

package me.saiintbrisson.minecraft

import me.devnatan.inventoryframework.context.IFContext

public inline fun ViewBuilder.slot(
    slot: Int,
    block: ViewSlotBuilder.() -> Unit
) {
    slots.add(ViewSlotBuilder(slot).apply(block))
}

/**
 * Called before the inventory is opened to the player.
 *
 * This handler is often called "pre-rendering" because it is possible to set the title and size
 * of the inventory and also cancel the opening of the View without even doing any handling related
 * to the inventory.
 *
 * It is not possible to manipulate the inventory in this handler, if it happens an exception
 * will be thrown.
 *
 * @param block The open view context handler.
 */
public fun ViewBuilder.onOpen(block: OpenViewContext.() -> Unit) {
    open = block
}

/**
 * Called when the player closes the view's inventory.
 *
 * It is possible to cancel this event and have the view's inventory open again for the player.
 *
 * @param block The close context handler.
 */
public fun ViewBuilder.onClose(block: IFContext.() -> Unit) {
    close = block
}

/**
 * Called when this view is rendered to the player for the first time.
 *
 * This is where you will define items that will be contained non-persistently in the context.
 *
 * Using [VirtualView.slot] here will cause a leak of items in memory or that the item that
 * was previously defined will be overwritten as the slot item definition method is for use in
 * the constructor only once. Instead, you should use the context item definition function
 * [VirtualView.slot].
 *
 * Handlers call order:
 * * [onOpen]
 * * this rendering function
 * * [onUpdate]
 * * [onClose]
 *
 * This is a rendering function and can modify the view's container, it's called once.
 *
 * @param block The render context handler.
 */
public fun ViewBuilder.onRender(block: ContextBlock) {
    render = block
}

/**
 * Called when the view is updated for a player.
 *
 * This is a rendering function and can modify the view's inventory.
 *
 * @param block The update context handler.
 * @see VirtualView.update
 */
public fun ViewBuilder.onUpdate(block: ContextBlock) {
    update = block
}

/**
 * Called when an actor clicks on a container while it has a view open.
 *
 * You can know if the click was on entity inventory or view inventory by
 * [IFSlotContext.isOnEntityContainer]
 *
 * Any function that triggers an [inventory modification][AbstractVirtualView.inventoryModificationTriggered]
 * is prohibited from being used in this handler.
 *
 * This context is cancelable and canceling this context will cancel the click, thus canceling
 * all subsequent interceptors causing the pipeline to terminate immediately.
 *
 * @param context The click context handler.
 */
public fun ViewBuilder.onClick(block: SlotContextBlock) {
    click = block
}

/**
 * Called when the player holds an item in the inventory.
 *
 * This handler will only work if the player manages to successfully hold the item, for example
 * it will not be called if the click has been canceled for whatever reasons.
 *
 * This context is non-cancelable.
 *
 * @param block The item hold context handler.
 */
public fun ViewBuilder.onItemHold(block: SlotContextBlock) {
    itemHold = block
}

/**
 * Called when an item is dropped by the player in an inventory (not necessarily the View's inventory).
 *
 * With this it is possible to detect if the player held and released an item:
 * * inside the view
 * * outside the view (in actor container)
 * * from inside to outside the view (to actor container)
 * * from outside to inside the view (from actor container)
 *
 * This handler is the counterpart of [onItemHold].
 *
 * @param block The item release context handler.
 */
public fun ViewBuilder.onItemRelease(block: ItemReleaseBlock) {
    itemRelease = block
}

/**
 * Called when a player moves a view item out of the view's container.
 *
 * Canceling the context will cancel the move.
 * Don't confuse moving with dropping.
 *
 * @param block The move out context handler.
 */
public fun ViewBuilder.onMoveOut(block: SlotMoveContextBlock) {
    moveOut = block
}
