package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public final class ClickOutsideInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(
		PipelineContext<BukkitClickViewSlotContext> pipeline,
		BukkitClickViewSlotContext subject
	) {
		final InventoryClickEvent clickEvent = subject.getClickOrigin();

		if (clickEvent.getSlotType() != InventoryType.SlotType.OUTSIDE)
			return;

		subject.getRoot().onClickOutside(subject);
	}

}
