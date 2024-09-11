package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.PluginDisableEvent;

@SuppressWarnings("unused")
final class IFInventoryListener implements Listener {

    private final ViewFrame viewFrame;

    public IFInventoryListener(ViewFrame viewFrame) {
        this.viewFrame = viewFrame;
    }

    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        if (!event.getPlugin().getName().equals(viewFrame.getOwner().getName())) return;

        viewFrame.unregister();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        final Player player = (Player) event.getWhoClicked();
        final Viewer viewer = viewFrame.getViewer(player);
        if (viewer == null) return;

        viewer.getActiveContext().simulateClick(event.getRawSlot(), viewer, event, false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        final Player player = (Player) event.getPlayer();
        final Viewer viewer = viewFrame.getViewer(player);
        if (viewer == null) return;

        final IFRenderContext context = viewer.getActiveContext();
        final RootView root = context.getRoot();
        final IFCloseContext closeContext = root.getElementFactory().createCloseContext(viewer, context);

        closeContext.simulateCloseForPlayer();
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onItemPickup(final PlayerPickupItemEvent event) {
        final Viewer viewer = viewFrame.getViewer(event.getPlayer());
        if (viewer == null) return;

        final IFContext context = viewer.getActiveContext();
        if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_PICKUP)) return;

        event.setCancelled(context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_PICKUP));
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(final PlayerDropItemEvent event) {
        final Viewer viewer = viewFrame.getViewer(event.getPlayer());
        if (viewer == null) return;

        final IFContext context = viewer.getActiveContext();
        if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_DROP)) return;

        event.setCancelled(context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_DROP));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        final Viewer viewer = viewFrame.getViewer((Player) event.getWhoClicked());
        if (viewer == null) return;

        final IFContext context = viewer.getActiveContext();
        if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_DRAG)) return;

        final boolean configValue = context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_DRAG);
        final int size = event.getInventory().getSize();
        for (final int rawSlot : event.getRawSlots()) {
            if (!(rawSlot < size)) continue;

            event.setCancelled(configValue);
            break;
        }
    }
}
