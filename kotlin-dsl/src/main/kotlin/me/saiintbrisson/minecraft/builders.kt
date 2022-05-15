@file:Suppress("FunctionName")

package me.saiintbrisson.minecraft

@ViewDsl
public class ViewBuilder {

    public var cancelOnClick: Boolean = false
    public var cancelOnPickup: Boolean = false
    public var cancelOnDrop: Boolean = false
    public var cancelOnDrag: Boolean = false
    public var cancelOnClone: Boolean = false
    public var cancelOnMoveIn: Boolean = false
    public var cancelOnMoveOut: Boolean = false
    public var cancelOnShiftClick: Boolean = false
    public var clearCursorOnClose: Boolean = false
    public var closeOnOutsideClick: Boolean = false

    internal var open: (OpenViewContext.() -> Unit)? = null
    internal var render: ContextBlock? = null
    internal var update: ContextBlock? = null
    internal var click: SlotContextBlock? = null
    internal var close: (CloseViewContext.() -> Unit)? = null
    internal var hotbarInteract: HotbarInteractBlock? = null
    internal var itemHold: SlotContextBlock? = null
    internal var itemRelease: ItemReleaseBlock? = null
    internal var moveOut: SlotMoveContextBlock? = null
    internal var moveIn: SlotMoveContextBlock? = null

    @PublishedApi
    internal var slots: MutableList<ViewSlotBuilder> = mutableListOf()

}

@ViewDsl
public class ViewSlotBuilder(@PublishedApi internal val slot: Int) {

    internal var render: SlotContextBlock? = null
    internal var update: SlotContextBlock? = null
    internal var click: SlotContextBlock? = null

    public fun toItem(): ViewItem {
        return ViewItem(slot).apply {
            setHandler(click) { clickHandler = it }
            setHandler(render) { renderHandler = it }
            setHandler(update) { updateHandler = it }
        }
    }

    private fun ViewItem.setHandler(
        currentHandler: SlotContextBlock?,
        assign: ViewItem.(ViewItemHandler) -> Unit
    ) = currentHandler?.let { it -> assign(it) }

}