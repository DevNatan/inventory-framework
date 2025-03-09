package me.devnatan.inventoryframework.context

import me.devnatan.inventoryframework.MinestomViewer
import me.devnatan.inventoryframework.RootView
import me.devnatan.inventoryframework.Viewer
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.jetbrains.annotations.ApiStatus

class SlotRenderContext
    @ApiStatus.Internal
    constructor(slot: Int, parent: IFRenderContext, private val viewer: Viewer?) :
    SlotContext(slot, parent), IFSlotRenderContext {
        override val player: Player = (viewer as MinestomViewer).player

        override var item: ItemStack = ItemStack.AIR
        private var cancelled = false
        private var changed = false
        private var forceUpdate = false

        override fun getResult(): ItemStack {
            return item
        }

        override fun isCancelled(): Boolean {
            return cancelled
        }

        override fun setCancelled(cancelled: Boolean) {
            this.cancelled = cancelled
        }

        override fun clear() {
            item = ItemStack.AIR
        }

        override fun hasChanged(): Boolean {
            return changed
        }

        override fun setChanged(changed: Boolean) {
            this.changed = changed
        }

        override fun isForceUpdate(): Boolean {
            return forceUpdate
        }

        override fun setForceUpdate(forceUpdate: Boolean) {
            this.forceUpdate = forceUpdate
        }

        override fun isOnEntityContainer(): Boolean {
            return container.isEntityContainer
        }

        override fun getViewer(): Viewer? {
            return viewer
        }

        override fun closeForPlayer() {
            parent.closeForPlayer()
        }

        override fun openForPlayer(other: Class<out RootView?>) {
            parent.openForPlayer(other)
        }

        override fun openForPlayer(
            other: Class<out RootView?>,
            initialData: Any,
        ) {
            parent.openForPlayer(other, initialData)
        }

        override fun updateTitleForPlayer(title: String) {
            parent.updateTitleForPlayer(title)
        }

        override fun resetTitleForPlayer() {
            parent.resetTitleForPlayer()
        }
    }
