package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;

final class IFInventoryListener implements Listener {

	private final ViewFrame viewFrame;

	public IFInventoryListener(ViewFrame viewFrame) {
		this.viewFrame = viewFrame;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryClick(final InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;

		final Player player = (Player) event.getWhoClicked();
		final Viewer viewer = viewFrame.getViewer(player);
		if (viewer == null) return;

		final IFContext context = viewer.getContext();
		final RootView root = context.getRoot();

		final Component component = context.getComponent(event.getRawSlot());
		if (component == null || !component.isVisible()) return;

		final ViewContainer container = event.getClickedInventory() instanceof PlayerInventory
			? viewer.getSelfContainer()
			: context.getContainer();

		final IFSlotClickContext slotContext = new SlotClickContext(
			root,
			container,
			viewer,
			context.getIndexedViewers(),
			event.getRawSlot(),
			context,
			component,
			event);

		root.getPipeline().execute(StandardPipelinePhases.CLICK, slotContext);
	}

	@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryClose(final InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;

		final Player player = (Player) event.getPlayer();
		final Viewer viewer = viewFrame.getViewer(player);
		if (viewer == null) return;

		final IFContext context = viewer.getContext();
		final RootView root = context.getRoot();

		final IFCloseContext closeContext = root.getElementFactory().createContext(
			root,
			context.getContainer(),
			viewer,
			context.getIndexedViewers(),
			IFCloseContext.class,
			context,
			context.getInitialData());

		root.getPipeline().execute(StandardPipelinePhases.CLOSE, closeContext);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onItemPickup(PlayerPickupItemEvent event) {
		final Viewer viewer = viewFrame.getViewer(event.getPlayer());
		if (viewer == null) return;

		final IFContext context = viewer.getContext();
		if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_PICKUP))
			return;

		event.setCancelled(context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_PICKUP));
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onItemDrop(PlayerDropItemEvent event) {
		final Viewer viewer = viewFrame.getViewer(event.getPlayer());
		if (viewer == null) return;

		final IFContext context = viewer.getContext();
		if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_DROP))
			return;

		event.setCancelled(context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_DROP));
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;

		final Viewer viewer = viewFrame.getViewer((Player) event.getWhoClicked());
		if (viewer == null) return;

		final IFContext context = viewer.getContext();
		if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_DRAG))
			return;

		final boolean configValue = context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_DRAG);
		final int size = event.getInventory().getSize();
		for (final int rawSlot : event.getRawSlots()) {
			if (!(rawSlot < size)) continue;

			event.setCancelled(configValue);
			break;
		}
	}
}
