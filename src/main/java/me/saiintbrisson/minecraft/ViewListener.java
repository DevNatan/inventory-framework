package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ViewListener implements Listener {

    private final ViewFrame frame;

    public ViewListener(final ViewFrame frame) {
        this.frame = frame;
    }

    private View getView(final Inventory inventory) {
        // check for Player#getTopInventory
        if (inventory == null)
            return null;

        final InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof View))
            return null;

        final View view = (View) holder;
        if (inventory.getType() != InventoryType.CHEST)
            throw new UnsupportedOperationException("Views is only supported on chest-type inventory.");

        return view;
    }

    @EventHandler
    public void onViewPluginDisable(final PluginDisableEvent e) {
        if (!frame.getOwner().equals(e.getPlugin()))
            return;

        // if the plugin is disabled it will not be possible to handle events
        frame.unregister();
    }

    @EventHandler(ignoreCancelled = true)
    public void onViewClick(final InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;

        final Player player = (Player) e.getWhoClicked();
        final Inventory inventory = e.getInventory();
        final View view = getView(inventory);
        if (view == null)
            return;

        if (e.getSlotType() == InventoryType.SlotType.OUTSIDE || e.getClick().isShiftClick()) {
            e.setCancelled(true);
            return;
        }

        if (!(e.getRawSlot() < inventory.getSize()))
            return;

        final ViewContext context = view.getContext(player);
        final int slot = e.getSlot();
        final ViewSlotContext globalClick = new DelegatedViewContext(context, slot, e.getCurrentItem());
        globalClick.setClickOrigin(e);
        view.onClick(globalClick);
        if (globalClick.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        ViewItem item = view.getItem(slot);
        if (item == null) {
            item = context.getItem(slot);
            if (item == null) {
                // cancel empty item place/pickup
                e.setCancelled(view.isCancelOnClick());
                return;
            }
        }

        if (item.getClickHandler() != null) {
            ViewSlotContext click = new DelegatedViewContext(context, slot, e.getCurrentItem());
            click.setClickOrigin(e);
            item.getClickHandler().handle(click);
            e.setCancelled(click.isCancelled());
        }

        e.setCancelled(item.isCancelOnClick());

        if (item.isCloseOnClick())
            player.closeInventory();
    }

    @EventHandler
    public void onViewClose(final InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player))
            return;

        final View view = getView(e.getInventory());
        if (view == null)
            return;

        final Player player = (Player) e.getPlayer();
        final ViewContext context = view.remove(player);
        if (context != null)
            view.onClose(context);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDropItemOnView(final PlayerDropItemEvent e) {
        final View view = getView(e.getPlayer().getOpenInventory().getTopInventory());
        if (view == null)
            return;

        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItemOnView(final PlayerPickupItemEvent e) {
        final View view = getView(e.getPlayer().getOpenInventory().getTopInventory());
        if (view == null)
            return;

        e.setCancelled(view.isCancelOnPickup());
    }

}
