package me.devnatan.inventoryframework.component

import me.devnatan.inventoryframework.Ref
import me.devnatan.inventoryframework.ViewContainer
import me.devnatan.inventoryframework.VirtualView
import me.devnatan.inventoryframework.context.Context
import me.devnatan.inventoryframework.context.IFRenderContext
import me.devnatan.inventoryframework.context.IFSlotClickContext
import me.devnatan.inventoryframework.context.IFSlotContext
import me.devnatan.inventoryframework.context.IFSlotRenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import me.devnatan.inventoryframework.context.SlotContext
import me.devnatan.inventoryframework.context.SlotRenderContext
import me.devnatan.inventoryframework.state.State
import me.devnatan.inventoryframework.utils.SlotConverter
import net.minestom.server.item.ItemStack
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.function.Supplier

class MinestomIemComponentBuilder private constructor(
    private val root: VirtualView,
    slot: Int,
    item: ItemStack?,
    renderHandler: Consumer<in IFSlotRenderContext>?,
    clickHandler: Consumer<in IFSlotClickContext>?,
    updateHandler: Consumer<in IFSlotContext>?,
    reference: Ref<Component>?,
    data: Map<String, Any>,
    cancelOnClick: Boolean,
    closeOnClick: Boolean,
    updateOnClick: Boolean,
    watchingStates: Set<State<*>>,
    isManagedExternally: Boolean,
    displayCondition: Predicate<Context>?
) : DefaultComponentBuilder<MinestomIemComponentBuilder, Context>(
        reference,
        data,
        cancelOnClick,
        closeOnClick,
        updateOnClick,
        watchingStates,
        isManagedExternally,
        displayCondition
    ), ItemComponentBuilder<MinestomIemComponentBuilder, Context>,
    ComponentFactory {
    private var slot: Int
    private var item: ItemStack?
    private var renderHandler: Consumer<in IFSlotRenderContext>?
    private var clickHandler: Consumer<in IFSlotClickContext>?
    private var updateHandler: Consumer<in IFSlotContext>?

    constructor(root: VirtualView) : this(
        root,
        -1,
        null,
        null,
        null,
        null,
        null,
        HashMap<String, Any>(),
        false,
        false,
        false,
        LinkedHashSet<State<*>>(),
        false,
        null
    )

    init {
        this.slot = slot
        this.item = item
        this.renderHandler = renderHandler
        this.clickHandler = clickHandler
        this.updateHandler = updateHandler
    }

    override fun toString(): String {
        return ("BukkitItemComponentBuilder{"
                + "slot=" + slot
                + ", item=" + item
                + ", renderHandler=" + renderHandler
                + ", clickHandler=" + clickHandler
                + ", updateHandler=" + updateHandler
                + "} " + super.toString())
    }

    override fun isContainedWithin(position: Int): Boolean {
        return position == slot
    }

    /**
     * {@inheritDoc}
     */
    override fun withSlot(slot: Int): MinestomIemComponentBuilder {
        this.slot = slot
        return this
    }

    override fun withSlot(row: Int, column: Int): MinestomIemComponentBuilder {
        val container: ViewContainer = (root as IFRenderContext).getContainer()
        return withSlot(SlotConverter.convertSlot(row, column, container.getRowsCount(), container.getColumnsCount()))
    }

    /**
     * Defines the item that will be used as fallback for rendering in the slot where this item is
     * positioned. The fallback item is always static.
     *
     * @param item The new fallback item stack.
     * @return This item builder.
     */
    fun withItem(item: ItemStack?): MinestomIemComponentBuilder {
        this.item = item
        return this
    }

    /**
     * Called when the item is rendered.
     *
     *
     * This handler is called every time the item or the view that owns it is updated.
     *
     * @param renderHandler The render handler.
     * @return This item builder.
     */
    @Suppress("UNCHECKED_CAST")
    fun onRender(renderHandler: Consumer<in SlotRenderContext>?): MinestomIemComponentBuilder {
        this.renderHandler = renderHandler as? Consumer<in IFSlotRenderContext>
        return this
    }

    /**
     * Dynamic rendering of a specific item.
     *
     *
     * This handler is called every time the item or the view that owns it is updated.
     *
     * @param renderFactory The render handler.
     * @return This item builder.
     */
    fun renderWith(renderFactory: Supplier<ItemStack>): MinestomIemComponentBuilder {
        return onRender { render: SlotRenderContext ->
            render.item = renderFactory.get()
        }
    }

    /**
     * Called when a player clicks on the item.
     *
     *
     * This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * @param clickHandler The click handler.
     * @return This item builder.
     */
    @Suppress("UNCHECKED_CAST")
    fun onClick(clickHandler: Consumer<in SlotClickContext>?): MinestomIemComponentBuilder {
        this.clickHandler = clickHandler as? Consumer<in IFSlotClickContext>
        return this
    }

    /**
     * Called when a player clicks on the item.
     *
     *
     * This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * @param clickHandler The click handler.
     * @return This item builder.
     */
    fun onClick(clickHandler: Runnable?): MinestomIemComponentBuilder {
        return onClick(if (clickHandler == null) null else Consumer { `$`: SlotClickContext? -> clickHandler.run() })
    }

    /**
     * Called when the item is updated.
     *
     * @param updateHandler The update handler.
     * @return This item builder.
     */
    @Suppress("UNCHECKED_CAST")
    fun onUpdate(updateHandler: Consumer<SlotContext>?): MinestomIemComponentBuilder {
        this.updateHandler = updateHandler as? Consumer<in IFSlotContext>
        return this
    }

    override fun create(): Component {
        return ItemComponent(
            root,
            slot,
            item,
            cancelOnClick,
            closeOnClick,
            displayCondition,
            renderHandler,
            updateHandler,
            clickHandler,
            watchingStates,
            isManagedExternally,
            updateOnClick,
            false,
            reference
        )
    }

    override fun copy(): MinestomIemComponentBuilder {
        return MinestomIemComponentBuilder(
            root,
            slot,
            item,
            renderHandler,
            clickHandler,
            updateHandler,
            reference,
            data,
            cancelOnClick,
            closeOnClick,
            updateOnClick,
            watchingStates,
            isManagedExternally,
            displayCondition
        )
    }
}
