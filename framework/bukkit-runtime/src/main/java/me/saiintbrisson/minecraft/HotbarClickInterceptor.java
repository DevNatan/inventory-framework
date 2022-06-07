package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class HotbarClickInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(PipelineContext<BukkitClickViewSlotContext> pipeline, BukkitClickViewSlotContext subject) {
		System.out.println("intercepted hotbar click");
		final InventoryClickEvent clickEvent = subject.getClickOrigin();

		if (clickEvent.getClick() != ClickType.NUMBER_KEY)
			return;

		subject.getRoot().onHotbarInteract(subject, clickEvent.getHotbarButton());
	}

}
