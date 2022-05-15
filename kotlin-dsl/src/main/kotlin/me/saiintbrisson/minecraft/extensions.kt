@file:Suppress("FunctionName")

package me.saiintbrisson.minecraft

internal typealias ContextBlock = @ViewDsl ViewContext.() -> Unit
internal typealias SlotContextBlock = @ViewDsl ViewSlotContext.() -> Unit
internal typealias SlotMoveContextBlock = @ViewDsl ViewSlotMoveContext.() -> Unit
internal typealias ItemReleaseBlock = @ViewDsl ViewSlotContext.(to: ViewSlotContext) -> Unit
internal typealias HotbarInteractBlock = @ViewDsl ViewSlotContext.(hotbarButton: Int) -> Unit

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

public inline fun ViewBuilder.slot(
    slot: Int,
    block: ViewSlotBuilder.() -> Unit
) {
    slots.add(ViewSlotBuilder(slot).apply(block))
}

public fun ViewBuilder.onOpen(block: OpenViewContext.() -> Unit) {
    open = block
}

public fun ViewBuilder.onClose(block: CloseViewContext.() -> Unit) {
    close = block
}

public fun ViewBuilder.onRender(block: ContextBlock) {
    render = block
}

public fun ViewBuilder.onUpdate(block: ContextBlock) {
    update = block
}

public fun ViewBuilder.onClick(block: SlotContextBlock) {
    click = block
}

public fun ViewBuilder.onHotbarInteract(block: HotbarInteractBlock) {
    hotbarInteract = block
}

public fun ViewBuilder.onItemHold(block: SlotContextBlock) {
    itemHold = block
}

public fun ViewBuilder.onItemRelease(block: ItemReleaseBlock) {
    itemRelease = block
}

public fun ViewBuilder.onMoveIn(block: SlotMoveContextBlock) {
    moveIn = block
}

public fun ViewBuilder.onMoveOut(block: SlotMoveContextBlock) {
    moveOut = block
}

public fun ViewSlotBuilder.onRender(block: SlotContextBlock) {
    render = block
}

public fun ViewSlotBuilder.onUpdate(block: SlotContextBlock) {
    update = block
}

public fun ViewSlotBuilder.onClick(block: SlotContextBlock) {
    click = block
}