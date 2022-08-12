package me.saiintbrisson.minecraft;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * A paginated context can originate from a view that supports paging.
 *
 * <p>Contains unique properties for a context like page, page count, page change and others. There
 * is no difference between a context and a paginated context, just some new properties but nothing
 * that changes its nature, life cycle or way of working.
 *
 * <p>A context can be {@link ViewContext#paginated() converted to a paginated context} if its root
 * is paginated.
 *
 * @param <T> The paginated item type.
 * @see ViewContext
 * @see AbstractPaginatedView
 */
public interface PaginatedViewContext<T> extends ViewContext, PaginatedVirtualView<T> {

    /**
     * An unmodifiable list with paginated data for the current page, never returns <code>null
     * </code> as there must be a valid context for getting data.
     *
     * @return An unmodifiable list with paginated data for the current page.
     */
    @NotNull
    List<T> getSource();

    /**
     * The current page index (starts from 0).
     *
     * @return The current page index of this context.
     */
    int getPage();

	@ApiStatus.Internal
	void setPage(int page);

    /**
     * The current number of items present on the current page.
     *
     * @return The current number of items present on the current page.
     */
    int getPageSize();

    /**
     * The maximum number of items that a page in a layered context can have.
     *
     * @return The maximum number of items that a page in a layered context can have.
     */
    int getPageMaxItemsCount();

    /**
     * The number of pages available in that context.
     *
     * @return The total page count.
     */
    int getPagesCount();

    /**
     * Returns the index of the previous page.
     *
     * @return The index of the previous page.
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    int getPreviousPage();

    /**
     * Returns the index of the next page.
     *
     * @return The index of the next page.
     */
    @Range(from = 1, to = Integer.MAX_VALUE)
    int getNextPage();

    /**
     * Checks if the current page is the first page of the available pages.
     *
     * @return If the current page is the first page of the available pages.
     */
    boolean hasPreviousPage();

    /**
     * Checks if there are more pages than the current one available.
     *
     * @return If there are more pages than the current one available.
     */
    boolean hasNextPage();

    /**
     * Checks that this context is on your first available page.
     *
     * @return Whether the {@link #getPage() current page} is the first page.
     */
    boolean isFirstPage();

    /**
     * Checks that this context is on your last available page.
     *
     * @return Whether the {@link #getPage() current page} is the last page.
     */
    boolean isLastPage();

    /**
     * Updates the current context by jumping to a specific page.
     *
     * @param page The page that'll be switched to.
     */
    void switchTo(@Range(from = 0, to = Integer.MAX_VALUE) int page);

    /**
     * Updates the current context by switching to the previous page.
     *
     * @return If page has been switched successfully, may return <code>false</code> if there are no
     *     more pages to go to.
     */
    boolean switchToPreviousPage();

    /**
     * Updates the current context by switching to the next page.
     *
     * @return If page has been switched successfully, may return <code>false</code> if there are no
     *     more pages to go to.
     */
    boolean switchToNextPage();

    /** {@inheritDoc} */
    @Override
    @NotNull
    AbstractPaginatedView<T> getRoot();
}
