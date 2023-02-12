package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.SlotClickContext;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player holds an item in the container's inventory.
 *
 * @see PipelineInterceptor
 */
final class GlobalItemHoldInterceptor implements PipelineInterceptor<SlotClickContext> {

    @Override
    public void intercept(@NotNull PipelineContext<SlotClickContext> pipeline, @NotNull SlotClickContext context) {
        if (context.isCancelled() || context.getComponent() == null) return;

        final InventoryClickEvent clickEvent = context.getClickOrigin();
        final InventoryAction action = clickEvent.getAction();

        // check for hold only on pickup or clone stack
        if (!(action.name().startsWith("PICKUP") || action == InventoryAction.CLONE_STACK)) return;

        final Component component = context.getComponent();
        if (component instanceof IFItem) {
            final IFItem<?> item = (IFItem<?>) component;
            item.setState(IFItem.State.HOLDING);

            if (item.getHoldHandler() != null) item.getHoldHandler().accept(context);
        }

        // TODO move global item hold interceptor to feature-hold-and-release
        //        final AbstractView root = context.getRoot();
        //		((View) context.getRoot()).onClose();
        //        root.onItemHold(context);
        clickEvent.setCancelled(context.isCancelled());
    }
}
