@file:Suppress("FunctionName")

package me.saiintbrisson.minecraft

internal typealias ContextBlock = ViewContext.() -> Unit
internal typealias SlotContextBlock = ViewSlotContext.() -> Unit
internal typealias SlotMoveContextBlock = ViewSlotMoveContext.() -> Unit
internal typealias ItemReleaseBlock = ViewSlotContext.(to: ViewSlotContext) -> Unit
internal typealias HotbarInteractBlock = ViewSlotContext.(hotbarButton: Int) -> Unit

@ViewDsl
public inline fun View(
    rows: Int = 0,
    title: String? = null,
    content: ViewBuilder.() -> Unit
): VirtualView {
    val view = View(rows, title)
    val builder = ViewBuilder().apply(content)
    builder.slots.forEach { view.with(it.toItem()) }
    return view
}

@SlotDsl
public inline fun ViewBuilder.slot(
    slot: Int,
    block: ViewSlotBuilder.() -> Unit
) {
    slots.add(ViewSlotBuilder(slot).apply(block))
}

@ViewDsl
public fun ViewBuilder.onOpen(block: OpenViewContext.() -> Unit) {
    open = block
}

@ViewDsl
public fun ViewBuilder.onClose(block: CloseViewContext.() -> Unit) {
    close = block
}

@ViewDsl
public fun ViewBuilder.onRender(block: ContextBlock) {
    render = block
}

@ViewDsl
public fun ViewBuilder.onUpdate(block: ContextBlock) {
    update = block
}

@ViewDsl
public fun ViewBuilder.onClick(block: SlotContextBlock) {
    click = block
}

@ViewDsl
public fun ViewBuilder.onHotbarInteract(block: HotbarInteractBlock) {
    hotbarInteract = block
}

@ViewDsl
public fun ViewBuilder.onItemHold(block: SlotContextBlock) {
    itemHold = block
}

@ViewDsl
public fun ViewBuilder.onItemRelease(block: ItemReleaseBlock) {
    itemRelease = block
}

@ViewDsl
public fun ViewBuilder.onMoveIn(block: SlotMoveContextBlock) {
    moveIn = block
}

@ViewDsl
public fun ViewBuilder.onMoveOut(block: SlotMoveContextBlock) {
    moveOut = block
}

@ContextDsl
public fun ViewSlotBuilder.onRender(block: SlotContextBlock) {
    render = block
}

@ContextDsl
public fun ViewSlotBuilder.onUpdate(block: SlotContextBlock) {
    update = block
}

@ContextDsl
public fun ViewSlotBuilder.onClick(block: SlotContextBlock) {
    click = block
}