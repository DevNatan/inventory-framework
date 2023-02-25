package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.IFItem;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on an item the view container.
 */
public final class ItemClickInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, @NotNull IFContext ctx) {
        if (!(ctx instanceof SlotClickContext))
            throw new IllegalArgumentException("Subject must be IFSlotClickContext");

        final SlotClickContext clickCtx = (SlotClickContext) ctx;
        final InventoryClickEvent event = clickCtx.getClickOrigin();
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;

        final Component component = clickCtx.getComponent();
        if (component == null) return;

        if (component instanceof IFItem<?>) {
            final IFItem<?> item = (IFItem<?>) component;
            // inherit cancellation so we can un-cancel it
            clickCtx.setCancelled(item.isCancelOnClick());
        }

        component.getInteractionHandler().clicked(component, clickCtx);
        event.setCancelled(clickCtx.isCancelled());
    }
}
