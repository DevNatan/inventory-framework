package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on the view container.
 * <p>
 * If the click is canceled, this interceptor ends the pipeline immediately.
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

		// inherit cancellation so we can un-cancel it
		subject.setCancelled(subject.getRoot().isCancelOnClick());

		subject.getRoot().onClick(subject);
		event.setCancelled(subject.isCancelled());

		if (!subject.isCancelled())
			return;

		pipeline.finish();
	}

}
