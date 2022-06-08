package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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

		subject.getRoot().onClickOutside(subject);
		if (!subject.isCancelled())
			return;

		clickEvent.setCancelled(true);
		pipeline.finish();
	}

}
