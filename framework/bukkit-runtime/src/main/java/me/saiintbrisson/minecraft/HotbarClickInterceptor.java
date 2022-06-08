package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Intercepts the player's hotbar click by launching
 * {@link View#onHotbarInteract(ViewContext, int)} when it happens.
 *
 * @see PipelineInterceptor
 */
public final class HotbarClickInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(
		PipelineContext<BukkitClickViewSlotContext> pipeline,
		BukkitClickViewSlotContext subject
	) {
		final InventoryClickEvent clickEvent = subject.getClickOrigin();

		if (clickEvent.getClick() != ClickType.NUMBER_KEY)
			return;

		subject.getRoot().onHotbarInteract(subject, clickEvent.getHotbarButton());
	}

}
