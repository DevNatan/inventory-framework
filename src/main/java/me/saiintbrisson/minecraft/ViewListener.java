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
import org.bukkit.scheduler.BukkitRunnable;

public class ViewListener implements Listener {

	private final ViewFrame frame;

	public ViewListener(final ViewFrame frame) {
		this.frame = frame;
	}

	private View getView(final Inventory inventory, final Player player) {
		// check for Player#getTopInventory
		if (inventory == null)
			return null;

		final InventoryHolder holder = inventory.getHolder();
		if (!(holder instanceof View))
			return null;

		final View view = (View) holder;
		if (inventory.getType() != InventoryType.CHEST)
			throw new UnsupportedOperationException("Views is only supported on chest-type inventory.");

		final ViewContext context = view.getContext(player);

		// for some reason I haven't figured out which one yet,
		// it's possible that the View's inventory is open and the context doesn't exist,
		// so we check to see if it's null
		if (context == null)
			return null;

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
		final View view = getView(inventory, (Player) e.getWhoClicked());
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
		final View view = getView(inventory, player);
		if (view == null)
			return;

		// TODO add option to close View on outside click
		if (e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
			e.setCancelled(true);
			return;
		}

		final InventoryAction action = e.getAction();
		if (action == InventoryAction.NOTHING)
			return;

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

			// for some reason I haven't figured out which one yet,
			// it's possible that the View's inventory is open and the context doesn't exist,
			// so we check to see if it's null
			if (context == null)
				return;

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

				for (final ViewItem holdingItem : view.getItems()) {
					if (holdingItem == null || holdingItem.getState() != ViewItem.State.HOLDING)
						continue;

					releaseAt(new DelegatedViewContext(context, slot, swappedItem == null ?
						e.getCurrentItem() : swappedItem), slot, cursor, e.getView().getBottomInventory());
				}

				if (view.isCancelOnMoveOut() || moveOutContext.isCancelled())
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

		final ViewContext context = view.getContext(player);

		// for some reason I haven't figured out which one yet,
		// it's possible that the View's inventory is open and the context doesn't exist,
		// so we check to see if it's null
		if (context == null) {
			return;
		}

		e.setCancelled(view.isCancelOnClick());
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

			if (view.isCancelOnMoveOut() || moveOutContext.isCancelled())
				e.setCancelled(true);

			if (moveOutContext.isMarkedToClose())
				Bukkit.getScheduler().runTask(frame.getOwner(), moveOutContext::closeNow);
			return;
		}

		final ViewItem item = view.resolve(context, slot);

		// global click handling
		final ViewSlotContext globalClick = new DelegatedViewContext(context, slot, stack);
		view.onClick(globalClick);

		e.setCancelled(e.isCancelled() || globalClick.isCancelled());
		if (item == null) {
			final ViewItem holdingItem = resolveReleasableItem(view, context);
			if (holdingItem != null)
				releaseAt(new DelegatedViewContext(context, holdingItem.getSlot(), stack), slot, cursor, e.getClickedInventory());

			return;
		}

		if (globalClick.isCancelled())
			return;

		final ViewSlotContext slotContext = new DelegatedViewContext(context, slot, stack);

		if (item.getClickHandler() != null) {
			item.getClickHandler().handle(slotContext);
			e.setCancelled(e.isCancelled() || slotContext.isCancelled());
		}

		if (item.isOverrideCancelOnClick())
			e.setCancelled(item.isCancelOnClick());

		if ((view.isCancelOnShiftClick() || item.isOverrideCancelOnShiftClick()) && click.isShiftClick())
			e.setCancelled(view.isCancelOnShiftClick() || item.isCancelOnShiftClick());

		if (!e.isCancelled()) {
			if (action.name().startsWith("PICKUP") || action == InventoryAction.CLONE_STACK) {
				item.setState(ViewItem.State.HOLDING);
				view.onItemHold(slotContext);
			} else if (item.getState() == ViewItem.State.HOLDING) {
				final ViewItem holdingItem = resolveReleasableItem(view, slotContext);
				if (holdingItem != null)
					releaseAt(slotContext, slot, cursor, e.getClickedInventory());
			}
		}

		if (item.isCloseOnClick() || slotContext.isMarkedToClose())
			Bukkit.getScheduler().runTask(frame.getOwner(), slotContext::closeNow);
	}

	private ViewItem resolveReleasableItem(View view, ViewContext context) {
		// we can't use `view.getRows()` here because the inventory size can be set dynamically
		// the items array size is updated when view is dynamic, so we can use this safely
		for (int i = 0; i < view.getItems().length; i++) {
			// must use resolve to works with context-defined items (not only items defined on View constructor)
			final ViewItem holdingItem = view.resolve(context, i);

			if (holdingItem == null || holdingItem.getState() != ViewItem.State.HOLDING)
				continue;

			return holdingItem;
		}

		return null;
	}

	private void releaseAt(ViewSlotContext context, int slot, ItemStack cursor, Inventory inventory) {
		context.getView().onItemRelease(context, new ViewSlotContext(context.getView(), context.getPlayer(),
			inventory, slot, cursor));

		final int currentSlot = context.getSlot();
		final ViewItem currentItem = context.getView().resolve(context, context.getSlot());
		context.getItems()[currentSlot] = null;

		if (currentItem == null)
			return;

		currentItem.setState(ViewItem.State.UNDEFINED);

		// outside top inventory
		if (slot > context.getInventory().getSize())
			return;

		context.getItems()[slot] = currentItem;
	}

	@EventHandler
	public void onViewClose(final InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player))
			return;

		final View view = getView(e.getInventory(), (Player) e.getPlayer());
		if (view == null)
			return;

		final Player player = (Player) e.getPlayer();
		final ViewContext context = view.getContext(player);
		if (context == null)
			return;

		final ViewContext close = new CloseViewContext(view, player, e.getInventory());
		view.onClose(close);

		if (close.isCancelled()) {
			new BukkitRunnable() {
				public void run() {
					player.openInventory(close.getInventory());
				}
			}.runTaskLater(frame.getOwner(), 1L);

			// set the old cursor item
			final ItemStack cursor = player.getItemOnCursor();

			// cursor can be null in legacy versions
			//noinspection ConstantConditions
			if ((cursor != null) && cursor.getType() != Material.AIR)
				player.setItemOnCursor(cursor);
			return;
		}

		if(view.isClearCursorOnClose()){
			player.setItemOnCursor(null);
		}

		view.remove(context);
	}

	@EventHandler
	public void onDropItemOnView(final PlayerDropItemEvent e) {
		final View view = getView(e.getPlayer().getOpenInventory().getTopInventory(), e.getPlayer());
		if (view == null)
			return;

		e.setCancelled(view.isCancelOnDrop());
	}

	@EventHandler
	public void onPickupItemOnView(final PlayerPickupItemEvent e) {
		final View view = getView(e.getPlayer().getOpenInventory().getTopInventory(), e.getPlayer());
		if (view == null)
			return;

		e.setCancelled(view.isCancelOnPickup());
	}

}
