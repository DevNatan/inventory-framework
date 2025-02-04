package me.devnatan.inventoryframework.context

import me.devnatan.inventoryframework.RootView
import me.devnatan.inventoryframework.ViewContainer
import me.devnatan.inventoryframework.Viewer
import me.devnatan.inventoryframework.component.Component
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack

class SlotClickContext constructor(
    slot: Int,
    parent: IFRenderContext,
    private val whoClicked: Viewer,
    private val clickedContainer: ViewContainer,
    private val clickedComponent: Component?,
    val clickOrigin: InventoryPreClickEvent,
    private val combined: Boolean
): SlotContext(slot, parent), IFSlotClickContext {
    private var cancelled = false

    override val player: Player
        /**
         * The player who clicked on the slot.
         */
        get() = clickOrigin.player

    override val item: ItemStack
        /**
         * The item that was clicked.
         */
        get() = clickOrigin.cursorItem

    override fun getComponent(): Component? {
        return clickedComponent
    }

    override fun getClickedContainer(): ViewContainer {
        return clickedContainer
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
        clickOrigin.isCancelled = cancelled
    }

    override fun getPlatformEvent(): Any {
        return clickOrigin
    }

    override fun getClickedSlot(): Int {
        return clickOrigin.slot
    }

    override fun isLeftClick(): Boolean {
        return clickOrigin.clickType == ClickType.LEFT_CLICK
    }

    override fun isRightClick(): Boolean {
        return clickOrigin.clickType == ClickType.RIGHT_CLICK
    }

    override fun isMiddleClick(): Boolean {
        return false
    }

    override fun isShiftClick(): Boolean {
        val clickType = clickOrigin.clickType
        return clickType == ClickType.SHIFT_CLICK
    }

    override fun isKeyboardClick(): Boolean {
        return clickOrigin.clickType == ClickType.CHANGE_HELD
    }

    override fun isOutsideClick(): Boolean {
        return clickOrigin.slot < 0;
    }

    override fun getClickIdentifier(): String {
        return clickOrigin.clickType.name
    }

    override fun isOnEntityContainer(): Boolean {
        return clickOrigin.inventory is PlayerInventory
    }

    override fun getViewer(): Viewer {
        return whoClicked
    }

    override fun closeForPlayer() {
        parent.closeForPlayer()
    }

    override fun openForPlayer(other: Class<out RootView?>) {
        parent.openForPlayer(other)
    }

    override fun openForPlayer(other: Class<out RootView?>, initialData: Any) {
        parent.openForPlayer(other, initialData)
    }

    override fun updateTitleForPlayer(title: String) {
        parent.updateTitleForPlayer(title)
    }

    override fun resetTitleForPlayer() {
        parent.resetTitleForPlayer()
    }

    override fun isCombined(): Boolean {
        return combined
    }
}
