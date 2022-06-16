@file:JvmName("ViewDslExtensions")
@file:Suppress("FunctionName")

package me.saiintbrisson.minecraft

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
internal annotation class ViewDsl

internal typealias ContextBlock = @ViewDsl ViewContext.() -> Unit
internal typealias SlotContextBlock = @ViewDsl ViewSlotContext.() -> Unit
internal typealias SlotMoveContextBlock = @ViewDsl ViewSlotMoveContext.() -> Unit
internal typealias ItemReleaseBlock = @ViewDsl ViewSlotContext.(to: ViewSlotContext) -> Unit
internal typealias HotbarInteractBlock = @ViewDsl ViewSlotContext.(hotbarButton: Int) -> Unit

@PublishedApi
internal val factory: ViewComponentFactory
    inline get() = PlatformUtils.getFactory()

public inline fun createView(
    rows: Int = 0,
    title: String? = null,
    type: ViewType = ViewType.CHEST,
    content: ViewBuilder.() -> Unit
): AbstractView {
    val view = factory.createView(rows, title, type)
    val builder = ViewBuilder().apply(content)
    builder.slots.forEach { view.with(it.toItem()) }
    return view
}

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
public fun ViewBuilder.onClose(block: CloseViewContext.() -> Unit) {
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
 * [ViewSlotContext.isOnEntityContainer]
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
 * This handler is the counterpart of [onMoveOut].
 *
 * @param block The item release context handler.
 */
public fun ViewBuilder.onMoveIn(block: SlotMoveContextBlock) {
    moveIn = block
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
 * Called when the item is moved from within the view's container to another container that is
 * not the view's container.
 *
 * This handler requires the [feature-move-io](https://github.com/DevNatan/inventory-framework/tree/main/feature-move-io)
 * feature module to be enabled to work properly.
 *
 * __Using item mutation functions in this handler is not allowed.__
 *
 * @param block The move in handler.
 */
public fun ViewSlotBuilder.onMoveIn(block: SlotMoveContextBlock) {
    moveIn = block
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
 * mutation functions, e.g.: [ViewSlotContext.setItem].
 *
 * An item can be re-rendered individually using [ViewSlotContext.updateSlot].
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
 * mutation functions, e.g.: [ViewSlotContext.setItem].
 *
 * An item can be updated individually using [ViewSlotContext.updateItem].
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
