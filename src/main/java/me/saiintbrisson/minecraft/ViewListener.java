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

import java.util.Objects;

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

		if (e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
			e.setCancelled(true);

			if (view.isCloseOnOutsideClick())
				view.close();

			final ViewContext context = view.getContext(player);
			if (context == null)
				return;

			context.close();
			return;
		}

		final InventoryAction action = e.getAction();
		if (action == InventoryAction.NOTHING)
			return;

		final ItemStack cursor = e.getCursor();
		final int slot = e.getSlot();

		final boolean bottomInventoryClick = !(e.getRawSlot() < inventory.getSize());
		if (!bottomInventoryClick && action == InventoryAction.CLONE_STACK && view.isCancelOnClone()) {
			e.setCancelled(true);
			return;
		}

		final ViewContext context = view.getContext(player);

		// for some reason I haven't figured out which one yet, it's possible that the View's inventory is open and
		// the context doesn't exist, so we check to see if it's null
		if (context == null) {
			return;
		}

		// move in and out handling
		if (bottomInventoryClick) {
			// context.getPlayer().sendMessage("Action: " + e.getAction());
			// context.getPlayer().sendMessage("Current item: " + e
			// .getCurrentItem());
			// context.getPlayer().sendMessage("Cursor item:" + e.getCursor());

			if (handleMoveIn(view, context, e))
				return;

			if (action != InventoryAction.PLACE_ALL &&
				action != InventoryAction.PLACE_ONE &&
				action != InventoryAction.PLACE_SOME &&
				action != InventoryAction.SWAP_WITH_CURSOR)
				return;

			// cannot to handle move in/out since item move not possible
			if (view.isCancelOnClick()) {
				e.setCancelled(true);
				return;
			}

			ItemStack swappedItem = null;
			if (action == InventoryAction.SWAP_WITH_CURSOR)
				swappedItem = e.getCurrentItem();

			// detect the item was being moved
			for (int i = view.getFirstSlot(); i <= view.getLastSlot(); i++) {
				final ViewItem item = view.resolve(context, i);
				if (item == null)
					continue;

				if (item.getState() != ViewItem.State.HOLDING)
					continue;

				final ViewSlotMoveContext moveOutContext = new ViewSlotMoveContext(context, item.getSlot(), cursor,
					e.getView().getBottomInventory(), swappedItem, slot, swappedItem != null, false);
				moveOutContext.runCatching(moveOutContext, () ->
					view.onMoveOut(moveOutContext));

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

			final ViewSlotMoveContext moveOutContext = new ViewSlotMoveContext(context, slot, stack, targetInventory,
				targetItem, slot, false, false);
			moveOutContext.runCatching(moveOutContext,
				() -> view.onMoveOut(moveOutContext));

			if (view.isCancelOnMoveOut() || moveOutContext.isCancelled())
				e.setCancelled(true);

			if (moveOutContext.isMarkedToClose())
				Bukkit.getScheduler().runTask(frame.getOwner(), moveOutContext::closeNow);
			return;
		}

		final ViewItem item = view.resolve(context, slot);

		// global click handling
		final ViewSlotContext globalClick = new DelegatedViewContext(context, slot, stack);
		globalClick.runCatching(globalClick, () -> view.onClick(globalClick));

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
			slotContext.runCatching(slotContext,
				() -> item.getClickHandler().handle(slotContext));
			e.setCancelled(e.isCancelled() || slotContext.isCancelled());
		}

		if (item.isOverrideCancelOnClick())
			e.setCancelled(item.isCancelOnClick());

		if ((view.isCancelOnShiftClick() || item.isOverrideCancelOnShiftClick()) && click.isShiftClick())
			e.setCancelled(view.isCancelOnShiftClick() || item.isCancelOnShiftClick());

		if (!e.isCancelled()) {
			if (action.name().startsWith("PICKUP") || action == InventoryAction.CLONE_STACK) {
				item.setState(ViewItem.State.HOLDING);
				slotContext.runCatching(slotContext, () ->
					view.onItemHold(slotContext));
			} else if (item.getState() == ViewItem.State.HOLDING) {
				final ViewItem holdingItem = resolveReleasableItem(view, slotContext);
				if (holdingItem != null)
					releaseAt(slotContext, slot, cursor, e.getClickedInventory());
			}
		}

		if (item.isCloseOnClick() || slotContext.isMarkedToClose())
			Bukkit.getScheduler().runTask(frame.getOwner(), slotContext::closeNow);
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

		final ViewContext close = new CloseViewContext(context);
		context.runCatching(context, () -> view.onClose(close));

		if (close.isCancelled()) {
			Bukkit.getScheduler().runTaskLater(
				frame.getOwner(),
				() -> player.openInventory(close.getInventory()),
				1L
			);

			// set the old cursor item
			final ItemStack cursor = player.getItemOnCursor();

			// cursor can be null in legacy versions
			//noinspection ConstantConditions
			if ((cursor != null) && cursor.getType() != Material.AIR)
				player.setItemOnCursor(cursor);
			return;
		}

		if (view.isClearCursorOnClose())
			player.setItemOnCursor(null);

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

	/**
	 * Handles the action of moving an item from the player's inventory to the view's inventory.
	 *
	 * @param view The view itself.
	 * @param context The current player context.
	 * @param event The event related to the movement.
	 */
	private boolean handleMoveIn(final View view, final ViewContext context, final InventoryClickEvent event) {
		if (!View.isFeatureEnabled(ViewFeature.MOVE_IN))
			return false;

		final InventoryAction action = event.getAction();

		// no need to handle hotbar swap
		if (action == InventoryAction.HOTBAR_SWAP)
			return false;

		// shift-clicked the item and moved it to the view's inventory
		if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			final ItemStack item = Objects.requireNonNull(event.getCurrentItem(),
				"Item cannot be null on move to other inventory action"
			);

			final SlotFindResult availableSlot = context.findNextAvailableSlot(item);

			// there is no slot available for the item to be moved, the event must be canceled and we return the method
			// as successful it is not possible to proceed but the move in was handled.
			if (!availableSlot.isAvailable()) {
				event.setCancelled(true);
				return true;
			}

			final ViewSlotMoveContext moveInContext = new ViewSlotMoveContext(context, event.getSlot(), item,
				event.getView().getTopInventory(), null, availableSlot.getValue(), false, false);
			moveInContext.runCatching(moveInContext, () ->
				view.onMoveIn(moveInContext));

			if (moveInContext.isCancelled()) {
				event.setCancelled(true);
				return true;
			}

			// in some cases the item must be moved, like PaginatedView with a defined layout
			// the slot that the item will be moved will be the slot that respects the conditions of that view
			if (availableSlot.shouldBeMoved()) {
				// TODO item should be moved to "move to"
			}

			return true;
		}

		return false;
	}

}
