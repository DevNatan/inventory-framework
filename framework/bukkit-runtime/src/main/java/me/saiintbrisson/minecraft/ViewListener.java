package me.saiintbrisson.minecraft;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

@RequiredArgsConstructor
class ViewListener implements Listener {

	private final Plugin plugin;
	private final CompatViewFrame<?> viewFrame;

	@Nullable
	private AbstractView getView(final @NotNull Player player) {
		return viewFrame.get(player);
	}

	private @NotNull ViewContext getContextOrThrow(
		@NotNull AbstractView view,
		@NotNull Player player
	) {
		final ViewContext context = view.getContext(target ->
			target.getViewers().stream()
				.map(viewer -> (BukkitViewer) viewer)
				.anyMatch(viewer -> viewer.getPlayer().getName().equals(player.getName()))
		);

		// for some reason I haven't figured out which one yet, it's possible
		// that the View's inventory is open and the context doesn't exist,
		// so we check to see if it's null
		if (context == null) {
			throw new IllegalStateException(String.format(
				"View context cannot be null in %s",
				view.getClass().getName()
			));
		}

		return context;
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void onHolderDisable(final PluginDisableEvent e) {
		if (!viewFrame.getOwner().equals(e.getPlugin()))
			return;

		viewFrame.unregister();
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void onDrag(final InventoryDragEvent e) {
		if (!(e.getWhoClicked() instanceof Player))
			return;

		final Inventory inventory = e.getInventory();
		final AbstractView view = getView((Player) e.getWhoClicked());
		if (view == null)
			return;

		// TODO implement pipeline for drag
		if (!view.isCancelOnDrag())
			return;

		final int size = inventory.getSize();
		for (int slot : e.getRawSlots()) {
			if (!(slot < size))
				continue;

			e.setCancelled(true);
			break;
		}
	}

	@EventHandler
	public void onClick(final InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		final Player player = (Player) event.getWhoClicked();
		final AbstractView view = getView(player);
		if (view == null)
			return;

		final BaseViewContext context;
		try {
			context = (BaseViewContext) getContextOrThrow(view, player);
		} catch (final IllegalStateException e) {
			event.setCancelled(true);
			e.printStackTrace();
			return;
		}

		final ViewSlotContext slotContext = new BukkitClickViewSlotContext(
			context.resolve(event.getSlot(), true,
				event.getClickedInventory() instanceof PlayerInventory
			),
			context,
			event
		);

		try {
			view.runCatching(context,
				() -> view.getPipeline().execute(AbstractView.CLICK, slotContext));
			plugin.getLogger().info("Pipeline launcher");
		} catch (final Throwable e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to execute click pipeline", e);
			return;
		}

		if (slotContext.getAttributes().isMarkedToClose())
			plugin.getServer().getScheduler().runTask(plugin, slotContext::closeUninterruptedly);
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void onViewClose(final InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player))
			return;

		final AbstractView view = getView((Player) e.getPlayer());
		if (view == null)
			return;

		final Player player = (Player) e.getPlayer();
		final ViewContext context = getContextOrThrow(view, player);
		final ViewContext close = new CloseViewContext(context);
		view.runCatching(context, () -> view.onClose(close));

		if (close.isCancelled()) {
			Bukkit.getScheduler().runTaskLater(
				viewFrame.getOwner(),
				() -> context.getViewers().stream()
					.filter(other -> other instanceof BukkitViewer &&
						((BukkitViewer) other).getPlayer().equals(player))
					.findFirst()
					.ifPresent(viewer -> close.getContainer().open(viewer)),
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

	@SuppressWarnings("unused")
	@EventHandler
	public void onDropItemOnView(final PlayerDropItemEvent e) {
		final AbstractView view = getView(e.getPlayer());
		if (view == null)
			return;

		e.setCancelled(view.isCancelOnDrop());
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void onPickupItemOnView(final PlayerPickupItemEvent e) {
		final AbstractView view = getView(e.getPlayer());
		if (view == null)
			return;

		e.setCancelled(view.isCancelOnPickup());
	}

}
