package me.devnatan.inventoryframework

import me.devnatan.inventoryframework.context.IFCloseContext
import me.devnatan.inventoryframework.context.IFContext
import me.devnatan.inventoryframework.context.IFRenderContext
import me.devnatan.inventoryframework.context.IFSlotClickContext
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.item.PickupItemEvent
import net.minestom.server.inventory.PlayerInventory

internal class IFInventoryListener(
    private val viewFrame: ViewFrame
) {

    init {
        val handler = MinecraftServer.getGlobalEventHandler()
        val inventoryNode = EventNode.type("IF-inventory", EventFilter.INVENTORY)
            .setPriority(10)
            .addListener(InventoryPreClickEvent::class.java, this::onInventoryClick)
            .addListener(InventoryCloseEvent::class.java, this::onInventoryClose)
        val playerNode = EventNode.type("IF-player", EventFilter.PLAYER)
        { _, p -> viewFrame.getViewer(p) != null }
            .setPriority(10)
            .addListener(ItemDropEvent::class.java, this::onItemDrop)
        val entityEvent = EventNode.type("IF-entity", EventFilter.ENTITY)
        { _, e -> e is Player && viewFrame.getViewer(e) != null }
            .setPriority(10)
            .addListener(PickupItemEvent::class.java, this::onItemPickup)
        handler.addChild(inventoryNode)
        handler.addChild(playerNode)
        handler.addChild(entityEvent)
    }

    fun onInventoryClick(event: InventoryPreClickEvent) {
        if (event.isCancelled) return

        val player = event.player
        val viewer = viewFrame.getViewer(player) ?: return

        val context: IFRenderContext = viewer.activeContext
        val clickedComponent: me.devnatan.inventoryframework.component.Component =
            context.getComponentsAt(event.slot).stream()
                .filter { obj: me.devnatan.inventoryframework.component.Component -> obj.isVisible }
                .findFirst()
                .orElse(null)
        val clickedContainer = if (event.inventory is PlayerInventory)
            viewer.selfContainer
        else
            context.getContainer()

        val root: RootView = context.getRoot()
        val clickContext: IFSlotClickContext = root.elementFactory
            .createSlotClickContext(event.slot, viewer, clickedContainer, clickedComponent, event, false)

        root.pipeline.execute(StandardPipelinePhases.CLICK, clickContext)
    }

    fun onInventoryClose(event: InventoryCloseEvent) {
        val player: Player = event.player
        val viewer = viewFrame.getViewer(player) ?: return

        val context: IFRenderContext = viewer.activeContext
        val root: RootView = context.getRoot()
        val closeContext: IFCloseContext = root.elementFactory.createCloseContext(viewer, context)

        root.pipeline.execute(StandardPipelinePhases.CLOSE, closeContext)
    }

    fun onItemPickup(event: PickupItemEvent) {
        val viewer = viewFrame.getViewer(event.entity as Player) ?: return

        val context: IFContext = viewer.activeContext
        if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_PICKUP)) return

        event.isCancelled = context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_PICKUP)
    }

    fun onItemDrop(event: ItemDropEvent) {
        val viewer = viewFrame.getViewer(event.getPlayer()) ?: return

        val context: IFContext = viewer.activeContext
        if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_DROP)) return

        event.isCancelled = context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_DROP)
    }
}
