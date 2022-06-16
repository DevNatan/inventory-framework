package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

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
	ItemWrapper getItem();

	void setItem(@Nullable Object item);

	void updateItem(Consumer<ItemWrapper> updater);

	boolean isLeftClick();

	boolean isRightClick();

	boolean isMiddleClick();

	boolean isShiftClick();

	boolean isKeyboardClick();

	boolean isOnEntityContainer();

	boolean isOutsideClick();

	@ApiStatus.Internal
	boolean hasChanged();

	@ApiStatus.Internal
	void setChanged(boolean changed);

	void updateSlot();

	/**
	 * Returns the value of a user-defined property for the item of this slot context
	 * or throws an exception if the property has not been set.
	 *
	 * @param key The property key.
	 * @param <T> The property value type.
	 * @return This item.
	 * @throws NoSuchElementException If the property has not been set.
	 */
	<T> T getItemData(@NotNull String key);

}