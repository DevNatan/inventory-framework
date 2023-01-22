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
        if (context.isCancelled() || context.getInternalItem() == null) return;

        final InventoryClickEvent clickEvent = context.getClickOrigin();
        final InventoryAction action = clickEvent.getAction();

        // check for hold only on pickup or clone stack
        if (!(action.name().startsWith("PICKUP") || action == InventoryAction.CLONE_STACK)) return;

        final IFItem<?> item = context.getInternalItem();
        item.setState(IFItem.State.HOLDING);

        if (item.getHoldHandler() != null) item.getHoldHandler().accept(context);

        // TODO move global item hold interceptor to feature-hold-and-release
        //        final AbstractView root = context.getRoot();
        //		((View) context.getRoot()).onClose();
        //        root.onItemHold(context);
        clickEvent.setCancelled(context.isCancelled());
    }
}
