@file:JvmSynthetic

package me.saiintbrisson.minecraft

/**
 * Called when a player holds an item.
 *
 * This handler works on any container that the actor has access to and only works if the
 * interaction has not been cancelled.
 *
 * You can check if the item has been released using [onItemRelease].
 *
 * __Using item mutation functions in this handler is not allowed.__
 *
 * @param block The item hold handler.
 */
public fun ViewSlotBuilder.onItemHold(block: SlotContextBlock) {
    itemHold = block
}

/**
 * Called when a player releases an item.
 *
 * This handler works on any container that the actor has access to and only works if the
 * interaction has not been cancelled.
 *
 * You can know when the item was hold using [onItemHold].
 *
 * __Using item mutation functions in this handler is not allowed.__
 *
 * @param block The item release handler.
 */
public fun ViewSlotBuilder.onItemRelease(block: ItemReleaseBlock) {
    itemRelease = block
}

/**
 * Called when this item is moved from outside to inside the view's container.
 *
 * This handler requires the [feature-move-io](https://github.com/DevNatan/inventory-framework/tree/main/feature-move-io)
 * feature module to be enabled to work properly.
 *
 * __Using item mutation functions in this handler is not allowed.__
 *
 * @param block The move out handler.
 */
public fun ViewSlotBuilder.onMoveOut(block: SlotMoveContextBlock) {
    moveOut = block
}

/**
 * Called when the item is rendered.
 *
 * This handler is called every time the item or the view that owns it is updated.
 *
 * It is allowed to change the item that will be displayed in this handler using the context
 * mutation functions, e.g.: [IFSlotContext.setItem].
 *
 * An item can be re-rendered individually using [IFSlotContext.updateSlot].
 *
 * @param block The render handler.
 * @return This item.
 */
public fun ViewSlotBuilder.onRender(block: SlotContextBlock) {
    render = block
}

/**
 * Called when the item is updated.
 *
 * It is allowed to change the item that will be displayed in this handler using the context
 * mutation functions, e.g.: [IFSlotContext.setItem].
 *
 * An item can be updated individually using [IFSlotContext.updateItem].
 *
 * @param block The update handler.
 */
public fun ViewSlotBuilder.onUpdate(block: SlotContextBlock) {
    update = block
}

/**
 * Called when a player clicks on the item.
 *
 * This handler works on any container that the actor has access to and only works if the
 * interaction has not been cancelled.
 *
 * __Using item mutation functions in this handler is not allowed.__
 *
 * @param block The click handler.
 */
public fun ViewSlotBuilder.onClick(block: SlotContextBlock) {
    click = block
}
