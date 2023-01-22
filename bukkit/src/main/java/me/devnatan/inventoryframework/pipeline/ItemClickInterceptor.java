package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.ViewSlotClickContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on an item the view container.
 */
final class ItemClickInterceptor implements PipelineInterceptor<ViewSlotClickContext> {

    @Override
    public void intercept(
            @NotNull PipelineContext<ViewSlotClickContext> pipeline, @NotNull ViewSlotClickContext context) {
        final InventoryClickEvent event = context.getClickOrigin();
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;

        final IFItem item = context.getBackingItem();
        if (item == null) return;

        // inherit cancellation so we can un-cancel it
        context.setCancelled(item.isCancelOnClick());

        if (item.getClickHandler() != null)
            context.getRoot().runCatching(context, () -> item.getClickHandler().accept(context));

        event.setCancelled(context.isCancelled());
    }
}
