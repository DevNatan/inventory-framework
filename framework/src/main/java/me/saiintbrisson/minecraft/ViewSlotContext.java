package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ViewSlotContext extends ViewContext {

	/**
	 * @deprecated Will be removed soon.
	 */
	@NotNull
	@Deprecated
	InventoryClickEvent getClickOrigin();

	boolean isCancelled();

	void setCancelled(boolean cancelled);

	int getSlot();

	@ApiStatus.Internal
	Object getItem();

	void setItem(@Nullable Object item);

	boolean isLeftClick();

	boolean isRightClick();

	boolean isMiddleClick();

	boolean isShiftClick();

	boolean isKeyboardClick();

	boolean isOnEntityContainer();

	boolean isOutsideClick();

	ViewSlotContext ref(String key);

	@ApiStatus.Internal
	boolean hasChanged();

	@ApiStatus.Internal
	void setChanged(boolean changed);

	void updateSlot();

}