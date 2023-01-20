package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.bukkit.ViewSlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on the view container.
 * If the click is canceled, this interceptor ends the pipeline immediately.
 */
final class GlobalClickInterceptor implements PipelineInterceptor<ViewSlotClickContext> {

    @Override
    public void intercept(
            @NotNull PipelineContext<ViewSlotClickContext> pipeline, @NotNull ViewSlotClickContext context) {
        final InventoryClickEvent event = context.getClickOrigin();

        // inherit cancellation so we can un-cancel it
        context.setCancelled(event.isCancelled() || context.getRoot().isCancelOnClick());

        context.getRoot().runCatching(context, () -> context.getRoot().onClick(context));
        event.setCancelled(context.isCancelled());
    }
}
