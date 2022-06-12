package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

public interface ViewSlotContext extends ViewContext {

	/**
	 * @deprecated Will be removed soon.
	 */
	@Deprecated
	InventoryClickEvent getClickOrigin();

	boolean isCancelled();

	void setCancelled(boolean cancelled);

	int getSlot();

	void setItem(@Nullable Object item);

	ViewItem withItem(@Nullable Object fallbackItem);

	boolean isLeftClick();

	boolean isRightClick();

	boolean isMiddleClick();

	boolean isShiftClick();

	boolean isKeyboardClick();

	boolean isOnEntityContainer();

}