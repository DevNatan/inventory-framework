package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on the view container.
 *
 * @see PipelineInterceptor
 */
final class GlobalClickInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(
		@NotNull PipelineContext<BukkitClickViewSlotContext> pipeline,
		BukkitClickViewSlotContext subject
	) {
		final InventoryClickEvent event = subject.getClickOrigin();
		event.setCancelled(subject.getRoot().isCancelOnClick());

		final ViewSlotContext globalClick = new BukkitClickViewSlotContext(subject, event);

		// inherit cancellation so we can un-cancel it
		globalClick.setCancelled(subject.isCancelled());

		subject.getRoot().onClick(globalClick);
		event.setCancelled(globalClick.isCancelled());

		if (!globalClick.isCancelled())
			return;

		pipeline.finish();
	}

}
