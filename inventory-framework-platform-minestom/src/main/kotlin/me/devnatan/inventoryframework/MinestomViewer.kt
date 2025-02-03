package me.devnatan.inventoryframework

import me.devnatan.inventoryframework.context.IFRenderContext
import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import java.util.*

class MinestomViewer(val player: Player, activeContext: IFRenderContext) : Viewer {
    private var selfContainer: ViewContainer? = null
    private var activeContext: IFRenderContext
    private val previousContexts: Deque<IFRenderContext> = LinkedList()
    private var lastInteractionInMillis: Long = 0
    private var transitioning = false

    init {
        this.activeContext = activeContext
    }

    override fun getActiveContext(): IFRenderContext {
        return activeContext
    }

    override fun setActiveContext(context: IFRenderContext) {
        this.activeContext = context
    }

    override fun getId(): String {
        return player.uuid.toString()
    }

    override fun open(container: ViewContainer) {
        player.openInventory((container as MinestomViewContainer).getInventory())
    }

    override fun close() {
        player.closeInventory()
    }

    override fun getSelfContainer(): ViewContainer {
        if (selfContainer == null) selfContainer = MinestomViewContainer(
            player.openInventory as Inventory, getActiveContext().isShared(), ViewType.PLAYER, false
        )
        return selfContainer!!
    }

    override fun getLastInteractionInMillis(): Long {
        return lastInteractionInMillis
    }

    override fun setLastInteractionInMillis(lastInteractionInMillis: Long) {
        this.lastInteractionInMillis = lastInteractionInMillis
    }

    override fun isBlockedByInteractionDelay(): Boolean {
        val configuredDelay: Long = activeContext.getConfig().getInteractionDelayInMillis()
        if (configuredDelay <= 0 || getLastInteractionInMillis() <= 0) return false

        return getLastInteractionInMillis() + configuredDelay >= System.currentTimeMillis()
    }

    override fun isTransitioning(): Boolean {
        return transitioning
    }

    override fun setTransitioning(transitioning: Boolean) {
        this.transitioning = transitioning
    }

    override fun getPreviousContext(): IFRenderContext? {
        return previousContexts.peekLast()
    }

    override fun setPreviousContext(previousContext: IFRenderContext) {
        previousContexts.add(previousContext)
    }

    override fun unsetPreviousContext() {
        previousContexts.pollLast()
    }

    override fun getPlatformInstance(): Any {
        return player
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as MinestomViewer
        return player == that.player
    }

    override fun hashCode(): Int {
        return player.hashCode()
    }

    override fun toString(): String {
        return ("BukkitViewer{"
                + "player=" + player
                + ", selfContainer=" + selfContainer
                + ", lastInteractionInMillis=" + lastInteractionInMillis
                + ", isTransitioning=" + transitioning
                + "}")
    }
}
