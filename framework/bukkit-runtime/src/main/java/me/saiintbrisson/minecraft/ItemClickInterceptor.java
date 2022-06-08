package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on an item the view container.
 *
 * @see PipelineInterceptor
 */
final class ItemClickInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(
		@NotNull PipelineContext<BukkitClickViewSlotContext> pipeline,
		BukkitClickViewSlotContext subject
	) {
		final InventoryClickEvent event = subject.getClickOrigin();
		final ViewItem item = subject.resolve(event.getSlot());
		if (item == null || item.getClickHandler() == null)
			return;

		final ViewSlotContext slotClick = new BukkitClickViewSlotContext(subject, event);

		// inherit cancellation so we can un-cancel it
		slotClick.setCancelled(subject.isCancelled() || item.isCancelOnClick());

		item.getClickHandler().handle(slotClick);
		event.setCancelled(slotClick.isCancelled());

		if (!slotClick.isCancelled())
			return;

		pipeline.finish();
	}

}
