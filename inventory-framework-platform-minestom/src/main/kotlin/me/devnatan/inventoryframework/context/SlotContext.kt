package me.devnatan.inventoryframework.context

import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfig
import me.devnatan.inventoryframework.ViewContainer
import me.devnatan.inventoryframework.Viewer
import me.devnatan.inventoryframework.component.Component
import me.devnatan.inventoryframework.state.State
import me.devnatan.inventoryframework.state.StateValue
import me.devnatan.inventoryframework.state.StateWatcher
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

abstract class SlotContext @ApiStatus.Internal protected constructor(
    private var slot: Int,
    private val parent: IFRenderContext
) : PlatformContext(),
    IFSlotContext, Context {
    abstract val item: ItemStack

    override fun getParent(): RenderContext {
        return parent as RenderContext
    }

    override fun getSlot(): Int {
        return slot
    }

    override fun setSlot(slot: Int) {
        this.slot = slot
    }

    override fun getIndexedViewers(): Map<String, Viewer> {
        return getParent().indexedViewers
    }

    override fun getTitle(): String {
        return getParent().title
    }

    override fun getComponents(): @UnmodifiableView MutableList<Component> {
        return getParent().components
    }

    override fun getInternalComponents(): List<Component> {
        return getParent().internalComponents
    }

    override fun getComponentsAt(position: Int): List<Component> {
        return getParent().getComponentsAt(position)
    }

    override fun addComponent(component: Component) {
        getParent().addComponent(component)
    }

    override fun removeComponent(component: Component) {
        getParent().removeComponent(component)
    }

    override fun renderComponent(component: Component) {
        getParent().renderComponent(component)
    }

    override fun updateComponent(component: Component, force: Boolean) {
        getParent().updateComponent(component, force)
    }

    override fun performClickInComponent(
        component: Component,
        viewer: Viewer,
        clickedContainer: ViewContainer,
        platformEvent: Any,
        clickedSlot: Int,
        combined: Boolean
    ) {
        getParent().performClickInComponent(component, viewer, clickedContainer, platformEvent, clickedSlot, combined)
    }

    override fun update() {
        getParent().update()
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

    override fun initializeState(id: Long, value: StateValue) {
        getParent().initializeState(id, value)
    }

    override fun updateState(id: Long, value: Any) {
        getParent().updateState(id, value)
    }

    override fun watchState(id: Long, listener: StateWatcher) {
        getParent().watchState(id, listener)
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

    override val allPlayers: List<Player>
        get() = getParent().allPlayers

    override fun updateTitleForPlayer(title: net.kyori.adventure.text.Component, player: Player) {
        getParent().updateTitleForPlayer(title, player)
    }

    override fun resetTitleForPlayer(player: Player) {
        getParent().resetTitleForPlayer(player)
    }

    override fun isActive(): Boolean {
        return getParent().isActive
    }

    override fun setActive(active: Boolean) {
        getParent().isActive = active
    }

    override fun isEndless(): Boolean {
        return getParent().isEndless
    }

    override fun setEndless(endless: Boolean) {
        getParent().isEndless = endless
    }

    override fun back() {
        getParent().back()
    }

    override fun back(initialData: Any) {
        getParent().back(initialData)
    }

    override fun canBack(): Boolean {
        return getParent().canBack()
    }
}
