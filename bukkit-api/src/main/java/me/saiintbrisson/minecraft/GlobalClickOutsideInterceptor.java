package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks outside the container's inventory.
 *
 * @see PipelineInterceptor
 */
final class GlobalClickOutsideInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

    @Override
    public void intercept(
            @NotNull PipelineContext<BukkitClickViewSlotContext> pipeline, BukkitClickViewSlotContext subject) {
        if (subject.isCancelled()) return;

        final InventoryClickEvent clickEvent = subject.getClickOrigin();
        if (clickEvent.getSlotType() != InventoryType.SlotType.OUTSIDE) return;

        final AbstractView root = subject.getRoot();
        root.onClickOutside(subject);
        clickEvent.setCancelled(subject.isCancelled());
    }
}
