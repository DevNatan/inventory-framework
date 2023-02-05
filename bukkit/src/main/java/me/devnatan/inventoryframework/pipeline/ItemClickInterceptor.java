package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.context.SlotClickContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on an item the view container.
 */
public final class ItemClickInterceptor implements PipelineInterceptor<SlotClickContext> {

    @Override
    public void intercept(@NotNull PipelineContext<SlotClickContext> pipeline, @NotNull SlotClickContext context) {
        final InventoryClickEvent event = context.getClickOrigin();
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;

        final IFItem<?> item = context.getInternalItem();
        if (item == null) return;

        // inherit cancellation so we can un-cancel it
        context.setCancelled(item.isCancelOnClick());

        if (item.getClickHandler() != null) item.getClickHandler().accept(context);

        event.setCancelled(context.isCancelled());
    }
}
