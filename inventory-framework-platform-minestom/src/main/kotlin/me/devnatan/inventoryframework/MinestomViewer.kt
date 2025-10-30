package me.devnatan.inventoryframework

import me.devnatan.inventoryframework.context.IFRenderContext
import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import java.util.Deque
import java.util.LinkedList

class MinestomViewer(
    val player: Player,
    private var activeContext: IFRenderContext?,
) : Viewer {
    private var selfContainer: ViewContainer? = null
    private val previousContexts: Deque<IFRenderContext> = LinkedList()
    private var lastInteractionInMillis: Long = 0
    private var switching = false
    private var interactionsLocked = false

    override fun getCurrentContext(): IFRenderContext =
        if (isSwitching()) {
            getPreviousContext() ?: error("Previous context cannot be null when switching")
        } else {
            getActiveContext()
        }

    override fun getActiveContext(): IFRenderContext = activeContext!!

    override fun setActiveContext(context: IFRenderContext) {
        this.activeContext = context
    }

    override fun getId(): String = player.uuid.toString()

    override fun open(container: ViewContainer) {
        player.openInventory((container as MinestomViewContainer).getInventory())
    }

    override fun close() {
        player.closeInventory()
    }

    override fun getSelfContainer(): ViewContainer {
        if (selfContainer == null) {
            selfContainer =
                MinestomViewContainer(
                    player.openInventory as Inventory,
                    getActiveContext().isShared(),
                    ViewType.PLAYER,
                    false,
                )
        }
        return selfContainer!!
    }

    override fun getLastInteractionInMillis(): Long = lastInteractionInMillis

    override fun setLastInteractionInMillis(lastInteractionInMillis: Long) {
        this.lastInteractionInMillis = lastInteractionInMillis
    }

    override fun isBlockedByInteractionDelay(): Boolean {
        val configuredDelay: Long =
            activeContext?.getConfig()?.interactionDelayInMillis ?: return false
        if (configuredDelay <= 0 || getLastInteractionInMillis() <= 0) return false

        return getLastInteractionInMillis() + configuredDelay >= System.currentTimeMillis()
    }

    override fun isSwitching(): Boolean = switching

    override fun setSwitching(switching: Boolean) {
        this.switching = switching
    }

    override fun isInteractionsLocked(): Boolean = interactionsLocked || isBlockedByInteractionDelay

    override fun setInteractionsLocked(interactionsLocked: Boolean) {
        this.interactionsLocked = interactionsLocked
    }

    override fun getPreviousContext(): IFRenderContext? = previousContexts.peekLast()

    override fun setPreviousContext(previousContext: IFRenderContext) {
        previousContexts.pollLast()
        previousContexts.add(previousContext)
    }

    override fun getPlatformInstance(): Any = player

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as MinestomViewer
        return player == that.player
    }

    override fun hashCode(): Int = player.hashCode()

    override fun toString(): String =
        (
            "BukkitViewer{" +
                "player=" +
                player +
                ", selfContainer=" +
                selfContainer +
                ", lastInteractionInMillis=" +
                lastInteractionInMillis +
                ", isSwitching=" +
                switching +
                "}"
        )
}
