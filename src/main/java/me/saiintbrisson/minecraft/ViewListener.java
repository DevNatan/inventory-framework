package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ViewListener implements Listener {

    private final ViewFrame frame;

    public ViewListener(ViewFrame frame) {
        this.frame = frame;
    }

    private View getView(Inventory inventory) {
        // check for Player#getTopInventory
        if (inventory == null)
            return null;

        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof View))
            return null;

        View view = (View) holder;
        if (inventory.getType() != InventoryType.CHEST)
            throw new UnsupportedOperationException("Views is only supported on chest-type inventory.");

        return view;
    }

    @EventHandler
    public void onViewPluginDisable(PluginDisableEvent e) {
        if (frame.getListener() == null || !frame.getOwner().equals(e.getPlugin()))
            return;

        // if the plugin is disabled it will not be possible to handle events
        frame.unregister();
    }

    @EventHandler(ignoreCancelled = true)
    public void onViewClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;

        View view = getView(e.getClickedInventory());
        if (view == null)
            return;

        Player player = (Player) e.getWhoClicked();
        int clickedSlot = e.getSlot();
        ViewSlotContext context = new ViewSlotContext(view, player, e.getClickedInventory(), clickedSlot, e.getCurrentItem());

        // moved to another inventory, not yet supported
        if (clickedSlot != e.getRawSlot()) {
            if (!context.isCancelled())
                e.setCancelled(true);
            return;
        }

        e.setCancelled(view.isCancelOnClick() || context.isCancelled());

        view.onClick(context, e);
        ViewItem item = view.getItem(clickedSlot);
        if (item != null) {
            if (item.getClickHandler() != null)
                item.getClickHandler().handle(context, e);

            e.setCancelled(item.isCancelOnClick());
        }
    }

    @EventHandler
    public void onViewClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player))
            return;

        View view = getView(e.getInventory());
        if (view == null)
            return;

        Player player = (Player) e.getPlayer();
        view.onClose(new ViewContext.NonCancellable(view, player, e.getInventory()));
        view.remove(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItemOnView(PlayerPickupItemEvent e) {
        View view = getView(e.getPlayer().getOpenInventory().getTopInventory());
        if (view == null)
            return;

        e.setCancelled(true);
    }

}
