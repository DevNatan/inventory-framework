package me.devnatan.inventoryframework.pagination;

import me.devnatan.inventoryframework.IFSlotContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a slot context that has a paged item attached to it, containing information for that
 * item.
 *
 * @param <T> The paginated item type.
 * @see IFPaginatedContext
 * @see IFSlotContext
 */
public interface IFPaginatedSlotContext<T> extends IFPaginatedContext<T>, IFSlotContext {

    @Override
    IFPaginatedContext<T> getParent();

    /**
     * Pagination index of the slot relative to the total amount of data.
     * <p>
     * Can be used identifier since it's unique for all pages.
     *
     * @return The pagination index (0 to source size).
     */
    long getIndex();

    /**
     * Pagination index of the slot for the current page.
     *
     * @return The pagination index (0 to page size).
     */
    int getIndexOnCurrentPage();

    /**
     * The paged value tied to this context.
     *
     * @return The paged value tied to this context.
     */
    T getValue();

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    IFPaginatedSlotContext<T> withItem(@Nullable Object item);
}
