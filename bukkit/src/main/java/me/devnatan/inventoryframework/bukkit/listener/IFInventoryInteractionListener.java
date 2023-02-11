package me.devnatan.inventoryframework.bukkit.listener;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@RequiredArgsConstructor
public final class IFInventoryInteractionListener implements Listener {

    private final ViewFrame viewFrame;

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        final Player player = (Player) event.getWhoClicked();
        final View view = viewFrame.getCurrentView(player);
        if (view == null) return;

        final ElementFactory elementFactory = view.getElementFactory();
        final Viewer viewer = elementFactory.createViewer(player);
        final IFContext context;
        try {
            context = view.getContext(viewer);
        } catch (final IllegalArgumentException exception) {
            event.setCancelled(true);
            throw exception;
        }

        final ViewContainer container = event.getClickedInventory() instanceof PlayerInventory
                ? viewer.getSelfContainer()
                : context.getContainer();
        final IFItem<?> item = context.getItem(event.getRawSlot());
        final IFSlotContext slotContext =
                elementFactory.createSlotContext(event.getRawSlot(), item, container, viewer, context);

        view.getPipeline().execute(StandardPipelinePhases.CLICK, slotContext);
    }
}
