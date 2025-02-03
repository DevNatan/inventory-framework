package me.devnatan.inventoryframework.pipeline

import me.devnatan.inventoryframework.ViewConfig
import me.devnatan.inventoryframework.VirtualView
import me.devnatan.inventoryframework.context.SlotClickContext
import net.minestom.server.event.inventory.InventoryPreClickEvent

/**
 * Intercepted when a player clicks on the view container.
 * If the click is canceled, this interceptor ends the pipeline immediately.
 */
class GlobalClickInterceptor : PipelineInterceptor<VirtualView> {
    override fun intercept(pipeline: PipelineContext<VirtualView>, subject: VirtualView) {
        if (subject !is SlotClickContext) return

        val context = subject as SlotClickContext
        val event: InventoryPreClickEvent = context.clickOrigin

        // inherit cancellation so we can un-cancel it
        context.isCancelled =
            event.isCancelled || context.config.isOptionSet(ViewConfig.CANCEL_ON_CLICK, true)
        context.root.onClick(context)
    }
}
