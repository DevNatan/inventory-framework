package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

        // TODO: properly handle shift click
        if (e.getSlotType() == InventoryType.SlotType.OUTSIDE || e.getClick().isShiftClick()) {
            e.setCancelled(true);
            return;
        }

        final InventoryAction action = e.getAction();
        if (action == InventoryAction.NOTHING)
            return;

        // player.sendMessage("Event: click=" + e.getClick() + ", action=" + action + ", item=" + e.getCurrentItem() + ", cursor=" + e.getCursor());
        final ItemStack cursor = e.getCursor();
        final int slot = e.getSlot();

        // bottom inventory click
        if (!(e.getRawSlot() < inventory.getSize())) {
            if (action != InventoryAction.PLACE_ALL &&
                    action != InventoryAction.PLACE_ONE &&
                    action != InventoryAction.PLACE_SOME &&
                    action != InventoryAction.SWAP_WITH_CURSOR)
                return;

            // unable to handle move out since item move not possible
            if (view.isCancelOnClick())
                return;

            final ViewContext context = view.getContext(player);
            for (int i = view.getFirstSlot(); i <= view.getLastSlot(); i++) {
                final ViewItem item = view.resolve(context, i);
                if (item == null)
                    continue;

                if (item.getState() != ViewItem.State.HOLDING)
                    continue;

                ItemStack swappedItem = null;
                if (action == InventoryAction.SWAP_WITH_CURSOR)
                    swappedItem = e.getCurrentItem();

                final ViewSlotMoveContext moveOutContext = new ViewSlotMoveContext(context, item.getSlot(), cursor, e.getView().getBottomInventory(), swappedItem, slot, swappedItem != null);
                view.onMoveOut(moveOutContext);
                item.setState(ViewItem.State.UNDEFINED);

                if (moveOutContext.isCancelled())
                    e.setCancelled(true);

                if (moveOutContext.isMarkedToClose())
                    Bukkit.getScheduler().runTask(frame.getOwner(), moveOutContext::closeNow);
                break;
            }
            return;
        }

        if (action == InventoryAction.CLONE_STACK && view.isCancelOnClone()) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(view.isCancelOnClick());

        final ViewContext context = view.getContext(player);
        final ItemStack stack = e.getCurrentItem();

        final ClickType click = e.getClick();
        if (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD ||
                click == ClickType.DROP ||
                click == ClickType.CONTROL_DROP) {
            ItemStack targetItem = null;
            final Inventory targetInventory = e.getView().getBottomInventory();
            if (action == InventoryAction.HOTBAR_MOVE_AND_READD)
                targetItem = targetInventory.getItem(e.getHotbarButton());

            final ViewSlotMoveContext moveOutContext = new ViewSlotMoveContext(context, slot, stack, targetInventory, targetItem, slot, false);
            view.onMoveOut(moveOutContext);

            if (moveOutContext.isCancelled())
                e.setCancelled(true);

            if (moveOutContext.isMarkedToClose())
                Bukkit.getScheduler().runTask(frame.getOwner(), moveOutContext::closeNow);
            return;
        }

        final ViewItem item = view.resolve(context, slot);

        // global click handling
        final ViewSlotContext globalClick = new DelegatedViewContext(context, slot, stack);
        globalClick.setClickOrigin(e);
        view.onClick(globalClick);

        if (item == null) {
            e.setCancelled(e.isCancelled() || globalClick.isCancelled());
            return;
        }

        if (action.name().startsWith("PICKUP") || action == InventoryAction.CLONE_STACK)
            item.setState(ViewItem.State.HOLDING);

        final ViewSlotContext slotContext = new DelegatedViewContext(context, slot, stack);
        if (item.getClickHandler() != null) {
            item.getClickHandler().handle(slotContext);
            e.setCancelled(e.isCancelled() || slotContext.isCancelled());
        }

        if (item.isOverrideCancelOnClick())
            e.setCancelled(item.isCancelOnClick());

        if (item.isCloseOnClick() || slotContext.isMarkedToClose())
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
            if (cursor != null && cursor.getType() != Material.AIR)
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
