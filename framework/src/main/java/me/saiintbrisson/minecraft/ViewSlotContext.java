package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface ViewSlotContext extends ViewContext {

	@Deprecated
	InventoryClickEvent getClickOrigin();

	boolean isCancelled();

	void setCancelled(final boolean cancelled);

	int getSlot();

}
