package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.ViewSlotClickContext;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player holds an item in the container's inventory.
 *
 * @see PipelineInterceptor
 */
final class GlobalItemHoldInterceptor implements PipelineInterceptor<ViewSlotClickContext> {

    @Override
    public void intercept(
            @NotNull PipelineContext<ViewSlotClickContext> pipeline, @NotNull ViewSlotClickContext context) {
        if (context.isCancelled() || context.getBackingItem() == null) return;

        final InventoryClickEvent clickEvent = context.getClickOrigin();
        final InventoryAction action = clickEvent.getAction();

        // check for hold only on pickup or clone stack
        if (!(action.name().startsWith("PICKUP") || action == InventoryAction.CLONE_STACK)) return;

        final IFItem item = context.getBackingItem();
        item.setState(IFItem.State.HOLDING);

        if (item.getItemHoldHandler() != null) item.getItemHoldHandler().accept(context);

        final AbstractView root = context.getRoot();
        root.onItemHold(context);
        clickEvent.setCancelled(context.isCancelled());
    }
}
