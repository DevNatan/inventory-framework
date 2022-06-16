package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Represents a slot context that has a paged item attached to it, containing information
 * for that item.
 *
 * @param <T> The paginated item type.
 * @see PaginatedViewContext
 * @see ViewSlotContext
 */
public interface PaginatedViewSlotContext<T> extends PaginatedViewContext<T>, ViewSlotContext {

	/**
	 * The position that the current item is in relation to the pagination.
	 * Please don't confuse with the position of the item in the container, this is {@link #getSlot()}.
	 *
	 * @return The item pagination index.
	 */
	int getIndex();

	/**
	 * The paged value tied to this context.
	 *
	 * @return The paged value tied to this context.
	 */
	T getValue();

	/**
	 * Defines a new item for this context, triggering an
	 * {@link AbstractVirtualView#inventoryModificationTriggered() inventory modification}.
	 * <p>
	 * If you need to change the item partially use {@link #updateItem(Consumer)}.
	 *
	 * @param item The new item that'll be set.
	 */
	@Override
	PaginatedViewSlotContext<T> withItem(@Nullable Object item);

}
