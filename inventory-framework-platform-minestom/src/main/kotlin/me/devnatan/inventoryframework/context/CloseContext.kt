package me.devnatan.inventoryframework.context

import me.devnatan.inventoryframework.MinestomViewer
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfig
import me.devnatan.inventoryframework.ViewContainer
import me.devnatan.inventoryframework.Viewer
import me.devnatan.inventoryframework.state.State
import me.devnatan.inventoryframework.state.StateValue
import me.devnatan.inventoryframework.state.StateWatcher
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

class CloseContext(subject: Viewer, private val parent: IFRenderContext) :
    PlatformConfinedContext(),
    IFCloseContext, Context {
    private val subject: Viewer = subject
    override val player: Player = (subject as MinestomViewer).player

    private var cancelled = false

    override val allPlayers: List<Player>
        get() = getParent().allPlayers

    override fun updateTitleForPlayer(title: Component, player: Player) {
        getParent().updateTitleForPlayer(title, player)
    }

    override fun resetTitleForPlayer(player: Player) {
        getParent().resetTitleForPlayer(player)
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    override fun getViewer(): Viewer {
        return subject
    }

    override fun getParent(): RenderContext {
        return parent as RenderContext
    }

    override fun getId(): UUID {
        return getParent().id
    }

    override fun getConfig(): ViewConfig {
        return getParent().config
    }

    override fun getContainer(): ViewContainer {
        return getParent().container
    }

    override fun getRoot(): View {
        return getParent().getRoot()
    }

    override fun getInitialData(): Any {
        return getParent().initialData
    }

    override fun setInitialData(initialData: Any) {
        getParent().initialData = initialData
    }

    override fun getStateValues(): @UnmodifiableView MutableMap<Long, StateValue>? {
        return getParent().stateValues
    }

    override fun initializeState(id: Long, value: StateValue) {
        getParent().initializeState(id, value)
    }

    override fun watchState(id: Long, listener: StateWatcher) {
        getParent().watchState(id, listener)
    }

    override fun getRawStateValue(state: State<*>?): Any {
        return getParent().getRawStateValue(state)
    }

    override fun getInternalStateValue(state: State<*>): StateValue {
        return getParent().getInternalStateValue(state)
    }

    override fun getUninitializedStateValue(stateId: Long): StateValue {
        return getParent().getUninitializedStateValue(stateId)
    }

    override fun updateState(id: Long, value: Any) {
        getParent().updateState(id, value)
    }

    override fun toString(): String {
        return ("CloseContext{" + "subject="
                + subject + ", player="
                + player + ", parent="
                + parent + ", cancelled="
                + cancelled + "} "
                + super.toString())
    }
}
