package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on an item the view container. Checks if the container should be
 * closed when the item is clicked.
 *
 * @see PipelineInterceptor
 */
final class CloseMarkInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

    @Override
    public void intercept(
            @NotNull PipelineContext<BukkitClickViewSlotContext> pipeline, BukkitClickViewSlotContext subject) {
        final InventoryClickEvent event = subject.getClickOrigin();
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;

        final ViewItem item = subject.getBackingItem();
        if (item == null) return;

        final boolean closeOnClick =
                item.isCloseOnClick() || subject.getAttributes().isMarkedToClose();
        if (!closeOnClick) return;

        subject.closeUninterruptedly();
        pipeline.finish();
    }
}
