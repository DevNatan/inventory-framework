@file:JvmName("ViewDslExtensions")
@file:Suppress("FunctionName")

package me.saiintbrisson.minecraft

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
internal annotation class ViewDsl

internal typealias ContextBlock = @ViewDsl ViewContext.() -> Unit
internal typealias SlotContextBlock<T> = @ViewDsl ViewSlotContext<T>.() -> Unit
internal typealias SlotMoveContextBlock<T> = @ViewDsl ViewSlotMoveContext<T>.() -> Unit
internal typealias ItemReleaseBlock<T> = @ViewDsl ViewSlotContext<T>.(to: ViewSlotContext<T>) -> Unit
internal typealias HotbarInteractBlock<T> = @ViewDsl ViewSlotContext<T>.(hotbarButton: Int) -> Unit

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

public fun <T> ViewBuilder.onOpen(block: OpenViewContext.() -> Unit) {
    open = block
}

public fun <T> ViewBuilder.onClose(block: CloseViewContext.() -> Unit) {
    close = block
}

public fun <T> ViewBuilder.onRender(block: ContextBlock) {
    render = block
}

public fun <T> ViewBuilder.onUpdate(block: ContextBlock) {
    update = block
}

public fun <T> ViewBuilder.onClick(block: SlotContextBlock<T>) {
    click = block
}

public fun <T> ViewBuilder.onHotbarInteract(block: HotbarInteractBlock<T>) {
    hotbarInteract = block
}

public fun <T : Any> ViewBuilder.onItemHold(block: SlotContextBlock<@UnsafeVariance T>) {
    itemHold = block
}

public fun <T> ViewBuilder.onItemRelease(block: ItemReleaseBlock<T>) {
    itemRelease = block
}

public fun <T> ViewBuilder.onMoveIn(block: SlotMoveContextBlock<T>) {
    moveIn = block
}

public fun <T> ViewBuilder.onMoveOut(block: SlotMoveContextBlock<T>) {
    moveOut = block
}

public fun <T> ViewSlotBuilder.onRender(block: SlotContextBlock<T>) {
    render = block
}

public fun <T> ViewSlotBuilder.onUpdate(block: SlotContextBlock<T>) {
    update = block
}

public fun <T> ViewSlotBuilder.onClick(block: SlotContextBlock<T>) {
    click = block
}
