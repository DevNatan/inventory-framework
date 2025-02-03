package me.devnatan.inventoryframework.pipeline

import me.devnatan.inventoryframework.VirtualView
import me.devnatan.inventoryframework.component.ItemComponent
import me.devnatan.inventoryframework.context.SlotClickContext
import net.minestom.server.event.inventory.InventoryPreClickEvent

/**
 * Intercepted when a player clicks on an item the view container.
 */
class ItemClickInterceptor : PipelineInterceptor<VirtualView> {
    override fun intercept(pipeline: PipelineContext<VirtualView>, subject: VirtualView) {
        if (subject !is SlotClickContext) return

        val context = subject as SlotClickContext
        val event: InventoryPreClickEvent = context.clickOrigin
        event.inventory ?: return

        val component = context.component ?: return

        if (component is ItemComponent) {
            val item: ItemComponent = component

            // inherit cancellation so we can un-cancel it
            context.isCancelled = item.isCancelOnClick
        }
    }
}
