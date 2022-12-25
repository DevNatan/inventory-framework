package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player holds an item in the container's inventory.
 *
 * @see PipelineInterceptor
 */
final class GlobalItemHoldInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

    @Override
    public void intercept(
            @NotNull PipelineContext<BukkitClickViewSlotContext> pipeline, BukkitClickViewSlotContext subject) {
        if (subject.isCancelled() || subject.getBackingItem() == null) return;

        final InventoryClickEvent clickEvent = subject.getClickOrigin();
        final InventoryAction action = clickEvent.getAction();

        // check for hold only on pickup or clone stack
        if (!(action.name().startsWith("PICKUP") || action == InventoryAction.CLONE_STACK)) return;

        final ViewItem item = subject.getBackingItem();
        item.setState(ViewItem.State.HOLDING);

        if (item.getItemHoldHandler() != null) item.getItemHoldHandler().accept(subject);

        final AbstractView root = subject.getRoot();
        root.onItemHold(subject);
        clickEvent.setCancelled(subject.isCancelled());
    }
}
