package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the player's hotbar click by launching
 * {@link View#onHotbarInteract(ViewContext, int)} when it happens.
 *
 * @see PipelineInterceptor
 */
final class GlobalHotbarClickInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(
		@NotNull PipelineContext<BukkitClickViewSlotContext> pipeline,
		BukkitClickViewSlotContext subject
	) {
		final InventoryClickEvent clickEvent = subject.getClickOrigin();

		if (clickEvent.getClick() != ClickType.NUMBER_KEY)
			return;

		subject.getRoot().onHotbarInteract(subject, clickEvent.getHotbarButton());
		if (!subject.isCancelled())
			return;

		clickEvent.setCancelled(true);
		pipeline.finish();
	}

}
