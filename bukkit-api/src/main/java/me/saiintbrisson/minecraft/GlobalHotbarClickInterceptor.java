package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the player's hotbar click by launching {@link View#onHotbarInteract(IFContext, int)}
 * when it happens.
 *
 * @see PipelineInterceptor
 */
final class GlobalHotbarClickInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

    @Override
    public void intercept(
            @NotNull PipelineContext<BukkitClickViewSlotContext> pipeline, BukkitClickViewSlotContext subject) {
        if (subject.isCancelled()) return;

        final InventoryClickEvent clickEvent = subject.getClickOrigin();

        if (clickEvent.getClick() != ClickType.NUMBER_KEY) return;

        subject.getRoot().onHotbarInteract(subject, clickEvent.getHotbarButton());
        clickEvent.setCancelled(subject.isCancelled());
    }
}
