package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.PlatformComponent;
import me.devnatan.inventoryframework.context.SlotClickContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on an item the view container.
 */
public final class ItemClickInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, @NotNull VirtualView subject) {
        if (!(subject instanceof SlotClickContext)) return;

        final SlotClickContext context = (SlotClickContext) subject;
        final InventoryClickEvent event = context.getClickOrigin();
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        if (context.getComponent() == null) return;
        if (!(context.getComponent() instanceof PlatformComponent)) return;

        // inherit cancellation so we can un-cancel it
        context.setCancelled(((PlatformComponent) context.getComponent()).isCancelOnClick());
    }
}
