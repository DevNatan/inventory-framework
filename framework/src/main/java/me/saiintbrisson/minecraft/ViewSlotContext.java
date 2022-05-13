package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

public interface ViewSlotContext extends ViewContext {

	@Deprecated
	InventoryClickEvent getClickOrigin();

	boolean isCancelled();

	void setCancelled(final boolean cancelled);

	int getSlot();

	default void setItem(@Nullable Object item) {}

}
