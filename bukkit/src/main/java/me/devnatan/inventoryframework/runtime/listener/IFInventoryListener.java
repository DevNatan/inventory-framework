package me.devnatan.inventoryframework.runtime.listener;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@RequiredArgsConstructor
public final class IFInventoryListener implements Listener {

    private final ViewFrame viewFrame;

    @SuppressWarnings("unused")
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        final Player player = (Player) event.getWhoClicked();
        final RootView root = viewFrame.getCurrentView(player);
        if (root == null) return;

        final ElementFactory elementFactory = root.getElementFactory();
        final Viewer viewer = elementFactory.createViewer(player);
        final IFContext mainContext;
        try {
            mainContext = root.getContext(viewer);
        } catch (final IllegalArgumentException exception) {
            event.setCancelled(true);
            throw exception;
        }

        final ViewContainer container = event.getClickedInventory() instanceof PlayerInventory
                ? viewer.getSelfContainer()
                : mainContext.getContainer();
        final Component component = mainContext.getComponent(event.getRawSlot());
        if (!component.isVisible()) return;

        final IFSlotClickContext slotContext =
                new SlotClickContext(root, container, viewer, event.getRawSlot(), mainContext, component, event);

        root.getPipeline().execute(StandardPipelinePhases.CLICK, slotContext);
    }

    @SuppressWarnings("unused")
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        final Player player = (Player) event.getPlayer();
        final RootView root = viewFrame.getCurrentView(player);
        if (root == null) return;

        final ElementFactory elementFactory = root.getElementFactory();
        final String viewerIdentifier = elementFactory.transformViewerIdentifier(player);
        final IFContext mainContext = root.getContextByViewer(viewerIdentifier);
        final Viewer viewer = mainContext.getIndexedViewers().get(viewerIdentifier);

        final IFCloseContext closeContext = elementFactory.createContext(
                root, mainContext.getContainer(), viewer, IFCloseContext.class, false, mainContext);

        root.getPipeline().execute(StandardPipelinePhases.CLOSE, closeContext);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemPickup(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final RootView root = viewFrame.getCurrentView(player);
        if (root == null) return;

        final IFContext context =
                root.getContextByViewer(root.getElementFactory().transformViewerIdentifier(player));

        if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_PICKUP)) return;

        event.setCancelled(context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_PICKUP));
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final RootView root = viewFrame.getCurrentView(player);
        if (root == null) return;

        final IFContext context =
                root.getContextByViewer(root.getElementFactory().transformViewerIdentifier(player));

        if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_DROP)) return;

        event.setCancelled(context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_DROP));
    }
}
