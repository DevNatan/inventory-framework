package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.IFItem;
import me.devnatan.inventoryframework.context.SlotClickContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on an item the view container. Checks if the container should be
 * closed when the item is clicked.
 */
public final class ItemCloseOnClickInterceptor implements PipelineInterceptor<SlotClickContext> {

    @Override
    public void intercept(@NotNull PipelineContext<SlotClickContext> pipeline, @NotNull SlotClickContext context) {
        final InventoryClickEvent event = context.getClickOrigin();
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;

        final Component component = context.getComponent();
        if (component == null) return;

        if (component instanceof IFItem<?>) {
            final IFItem<?> item = (IFItem<?>) component;
            if (!item.isCloseOnClick()) return;
        }

        context.close();
        pipeline.finish();
    }
}
