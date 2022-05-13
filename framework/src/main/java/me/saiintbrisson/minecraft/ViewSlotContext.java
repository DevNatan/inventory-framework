package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ViewSlotContext extends ViewContext {

	@Deprecated
	InventoryClickEvent getClickOrigin();

	boolean isCancelled();

	void setCancelled(final boolean cancelled);

	int getSlot();

	void setItem(@Nullable Object item);

}
