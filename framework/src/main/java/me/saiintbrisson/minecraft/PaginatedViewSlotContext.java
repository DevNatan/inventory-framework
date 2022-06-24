package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a slot context that has a paged item attached to it, containing information for that
 * item.
 *
 * @param <T> The paginated item type.
 * @see PaginatedViewContext
 * @see ViewSlotContext
 */
public interface PaginatedViewSlotContext<T> extends PaginatedViewContext<T>, ViewSlotContext {

    /**
     * The position that the current item is in relation to the pagination. Please don't confuse
     * with the position of the item in the container, this is {@link #getSlot()}.
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

    /** {@inheritDoc} */
    @Override
    PaginatedViewSlotContext<T> withItem(@Nullable Object item);
}
