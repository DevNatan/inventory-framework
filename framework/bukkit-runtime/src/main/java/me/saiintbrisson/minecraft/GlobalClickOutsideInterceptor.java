package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks outside the container's inventory.
 *
 * @see PipelineInterceptor
 */
final class GlobalClickOutsideInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(
		@NotNull PipelineContext<BukkitClickViewSlotContext> pipeline,
		BukkitClickViewSlotContext subject
	) {
		final InventoryClickEvent clickEvent = subject.getClickOrigin();
		if (clickEvent.getSlotType() != InventoryType.SlotType.OUTSIDE)
			return;

		final AbstractView root = subject.getRoot();
		root.onClickOutside(subject);
		clickEvent.setCancelled(subject.isCancelled());

		if (root.isCloseOnOutsideClick()) {
			final Plugin plugin = (Plugin) root.getViewFrame().getOwner();
			plugin.getServer().getScheduler().runTask(plugin, subject::close);
			return;
		}

		if (!subject.isCancelled())
			return;

		pipeline.finish();
	}

}
