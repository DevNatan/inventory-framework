package me.devnatan.inventoryframework.pipeline;

import static me.devnatan.inventoryframework.RootView.CancelOnClick;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.ViewSlotClickContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on the view container.
 * If the click is canceled, this interceptor ends the pipeline immediately.
 */
public final class GlobalClickInterceptor implements PipelineInterceptor<ViewSlotClickContext> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void intercept(
            @NotNull PipelineContext<ViewSlotClickContext> pipeline, @NotNull ViewSlotClickContext context) {
        final InventoryClickEvent event = context.getClickOrigin();

        // inherit cancellation so we can un-cancel it
        context.setCancelled(
                event.isCancelled() || context.getRoot().getConfig().isOptionSet(CancelOnClick));
        ((PlatformView) context.getRoot()).onClick(context);
        event.setCancelled(context.isCancelled());
    }
}
