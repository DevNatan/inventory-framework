package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.ViewSlotClickContext;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the player's hotbar click.
 */
final class GlobalHotbarClickInterceptor implements PipelineInterceptor<ViewSlotClickContext> {

    @Override
    public void intercept(
            @NotNull PipelineContext<ViewSlotClickContext> pipeline, @NotNull ViewSlotClickContext subject) {
        if (subject.isCancelled()) return;

        final InventoryClickEvent clickEvent = subject.getClickOrigin();

        if (clickEvent.getClick() != ClickType.NUMBER_KEY) return;

        subject.getRoot().onHotbarInteract(subject, clickEvent.getHotbarButton());
        clickEvent.setCancelled(subject.isCancelled());
    }
}
