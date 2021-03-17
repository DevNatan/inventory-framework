package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

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

        frame.unregister();
    }

    @EventHandler
    public void onViewItemDrag(final InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;

        final Inventory inventory = e.getInventory();
        final View view = getView(inventory);
        if (view == null)
            return;

        final int size = inventory.getSize();
        for (int slot : e.getRawSlots()) {
            if (!(slot < size))
                continue;

            if (view.isCancelOnDrag()) {
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
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

        e.setCancelled(view.isCancelOnClick());

        final ItemStack stack = e.getCurrentItem();
        final ViewContext context = view.getContext(player);
        final int slot = e.getSlot();
        final ViewItem item = view.resolve(context, slot);
        final ViewSlotContext globalClick = new DelegatedViewContext(context, slot, stack);
        globalClick.setClickOrigin(e);
        view.onClick(globalClick);

        if (item == null) {
            e.setCancelled(e.isCancelled() || globalClick.isCancelled());
            return;
        }

        if (item.getClickHandler() != null) {
            final ViewSlotContext internalClick = new DelegatedViewContext(context, slot, stack);
            item.getClickHandler().handle(internalClick);
            e.setCancelled(e.isCancelled() || internalClick.isCancelled());
        }

        e.setCancelled(e.isCancelled() || item.isCancelOnClick());

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
        if (context != null) {
            final ItemStack cursor = player.getItemOnCursor();
            if (cursor != null)
                player.setItemOnCursor(null);

            view.onClose(context);
        }
    }

    @EventHandler
    public void onDropItemOnView(final PlayerDropItemEvent e) {
        final View view = getView(e.getPlayer().getOpenInventory().getTopInventory());
        if (view == null)
            return;

        e.setCancelled(view.isCancelOnDrop());
    }

    @EventHandler
    public void onPickupItemOnView(final PlayerPickupItemEvent e) {
        final View view = getView(e.getPlayer().getOpenInventory().getTopInventory());
        if (view == null)
            return;

        e.setCancelled(view.isCancelOnPickup());
    }

}
