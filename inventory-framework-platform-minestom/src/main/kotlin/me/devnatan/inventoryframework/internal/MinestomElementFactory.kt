package me.devnatan.inventoryframework.internal

import me.devnatan.inventoryframework.*
import me.devnatan.inventoryframework.component.MinestomIemComponentBuilder
import me.devnatan.inventoryframework.component.Component
import me.devnatan.inventoryframework.component.ComponentBuilder
import me.devnatan.inventoryframework.context.*
import me.devnatan.inventoryframework.logging.Logger
import me.devnatan.inventoryframework.logging.NoopLogger
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

class MinestomElementFactory : ElementFactory() {
    private var worksInCurrentPlatform: Boolean? = null

    override fun createUninitializedRoot(): RootView {
        return View()
    }

    // TODO Test it
    override fun createContainer(context: IFContext): ViewContainer {
        val config: ViewConfig = context.getConfig()
        val finalType: ViewType = config.type ?: defaultType

        val size: Int = finalType.normalize(config.getSize())
        require(!(size != 0 && !finalType.isExtendable())) {
            String.format(
                ("Only \"%s\" type can have a custom size,"
                        + " \"%s\" always have a size of %d. Remove the parameter that specifies the size"
                        + " of the container on %s or just set the type explicitly."),
                ViewType.CHEST.getIdentifier(),
                finalType.getIdentifier(),
                finalType.getMaxSize(),
                context.getRoot().javaClass.getName()
            )
        }


        val type = when (finalType) {
            ViewType.CHEST -> {
                when (size / finalType.columns) {
                    1 -> InventoryType.CHEST_1_ROW
                    2 -> InventoryType.CHEST_2_ROW
                    3 -> InventoryType.CHEST_3_ROW
                    4 -> InventoryType.CHEST_4_ROW
                    5 -> InventoryType.CHEST_5_ROW
                    6 -> InventoryType.CHEST_6_ROW
                    else -> InventoryType.CHEST_6_ROW
                }
            }
            ViewType.BEACON -> InventoryType.BEACON
            ViewType.HOPPER -> InventoryType.HOPPER
            ViewType.SMOKER -> InventoryType.SMOKER
            ViewType.BLAST_FURNACE -> InventoryType.BLAST_FURNACE
            ViewType.FURNACE -> InventoryType.FURNACE
            ViewType.ANVIL -> InventoryType.ANVIL
            ViewType.CRAFTING_TABLE -> InventoryType.CRAFTING
            ViewType.DROPPER, ViewType.DROPPER -> InventoryType.WINDOW_3X3
            ViewType.BREWING_STAND -> InventoryType.BREWING_STAND
            ViewType.SHULKER_BOX -> InventoryType.SHULKER_BOX
            else -> error("Unsupported type: $finalType")
        }

        val inventory = Inventory(type, net.kyori.adventure.text.Component.empty())
        return MinestomViewContainer(inventory, false, finalType, false)
    }

    override fun createViewer(entity: Any, context: IFRenderContext): Viewer {
        require(entity is Player) { "createViewer(...) first parameter must be a Player" }

        return MinestomViewer(entity as Player, context)
    }

    override fun createOpenContext(
        root: RootView, subject: Viewer?, viewers: List<Viewer>, initialData: Any
    ): IFOpenContext {
        return OpenContext(
            root as View,
            subject,
            viewers.stream().collect(
                Collectors.toMap<Viewer, String, Viewer>(
                    Function<Viewer, String> { obj: Viewer -> obj.getId() }, Function.identity<Viewer>()
                )
            ),
            initialData
        )
    }

    override fun createRenderContext(
        id: UUID,
        root: RootView,
        config: ViewConfig,
        container: ViewContainer,
        viewers: Map<String, Viewer>,
        subject: Viewer,
        initialData: Any
    ): IFRenderContext {
        return RenderContext(id, root as View, config, container, viewers, subject, initialData)
    }

    override fun createSlotClickContext(
        slotClicked: Int,
        whoClicked: Viewer,
        interactionContainer: ViewContainer,
        componentClicked: Component?,
        origin: Any,
        combined: Boolean
    ): IFSlotClickContext {
        val context: IFRenderContext = whoClicked.getActiveContext()
        return SlotClickContext(
            slotClicked,
            context,
            whoClicked,
            interactionContainer,
            componentClicked,
            origin as InventoryPreClickEvent,
            combined
        )
    }

    override fun createSlotRenderContext(
        slot: Int, parent: IFRenderContext, viewer: Viewer?
    ): IFSlotRenderContext {
        return SlotRenderContext(slot, parent, viewer)
    }

    override fun createCloseContext(viewer: Viewer, parent: IFRenderContext): IFCloseContext {
        return CloseContext(viewer, parent)
    }

    override fun createComponentBuilder(root: VirtualView): ComponentBuilder<*, Context> {
        return MinestomIemComponentBuilder(root)
    }

    override fun worksInCurrentPlatform(): Boolean {
        return true
    }

    override fun getLogger(): Logger {
        return NoopLogger()
    }

    override fun scheduleJobInterval(root: RootView, intervalInTicks: Long, execution: Runnable): Job {
        return MinestomTaskJobImpl(intervalInTicks.toInt(), execution)
    }

    companion object {
        private val defaultType: ViewType = ViewType.CHEST
    }
}
