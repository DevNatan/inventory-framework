package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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
		System.out.println("Item click interceptor");
		final InventoryClickEvent event = subject.getClickOrigin();
		if (event.getSlotType() == InventoryType.SlotType.OUTSIDE)
			return;

		final ViewItem item = subject.getBackingItem();
		if (item == null)
			return;

		// inherit cancellation so we can un-cancel it
		subject.setCancelled(item.isCancelOnClick());
		System.out.println("Cancel on click backing item is not null ;')");
		System.out.println(item);

		if (item.getClickHandler() != null)
			subject.getRoot().runCatching(subject, () -> item.getClickHandler().accept(subject));

		event.setCancelled(subject.isCancelled());
	}

}
