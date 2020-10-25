package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
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
        if (frame.getListener() == null || !frame.getOwner().equals(e.getPlugin()))
            return;

        // if the plugin is disabled it will not be possible to handle events
        frame.unregister();
    }

    @EventHandler(ignoreCancelled = true)
    public void onViewClick(final InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;

        final Inventory inv = e.getClickedInventory();
        if (inv == null)
            return; // clicked to the outside

        int clickedSlot = e.getSlot();
        if (clickedSlot >= inv.getSize())
            return;  // array index out of bounds: -999???!

        final View view = getView(inv);
        if (view == null)
            return;

        // moved to another inventory, not yet supported
        if (clickedSlot != e.getRawSlot()) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(view.isCancelOnClick());

        final Player player = (Player) e.getWhoClicked();
        final ViewContext context = view.getContext(player);
        ViewItem item = view.getItem(clickedSlot);

        if (item == null) {
            item = context.getItem(clickedSlot);
            if (item == null) {
                ViewSlotContext slotContext = new SynchronizedViewContext(context, clickedSlot, e.getCurrentItem());
                slotContext.setClickOrigin(e);
                view.onClick(slotContext);
                e.setCancelled(context.isCancelled());
                return;
            }
        }

        if (item.getClickHandler() != null) {
            ViewSlotContext slotContext = new SynchronizedViewContext(context, clickedSlot, e.getCurrentItem());
            slotContext.setClickOrigin(e);
            item.getClickHandler().handle(slotContext);
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
        view.onClose(new ViewContext(view, player, e.getInventory()));
        view.remove(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItemOnView(final PlayerPickupItemEvent e) {
        final View view = getView(e.getPlayer().getOpenInventory().getTopInventory());
        if (view == null)
            return;

        e.setCancelled(true);
    }

}
