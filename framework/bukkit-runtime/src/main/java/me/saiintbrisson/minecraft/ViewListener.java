package me.saiintbrisson.minecraft;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.PluginDisableEvent;
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

}
