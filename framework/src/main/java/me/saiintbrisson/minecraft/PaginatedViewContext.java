package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Range;

import java.util.List;

/**
 * A paging context can originate from a view that supports paging.
 *
 * <p>It contains unique properties for a context like page, page count, page change and others.
 * There is no difference between a context and a paginated context, just some new properties but
 * nothing that changes its nature, life cycle or way of working.
 *
 * <p>A context can be converted to a paginated context if its root is paginated. See {@link
 * ViewContext#paginated()}.
 *
 * @param <T> The paginated item type.
 * @see ViewContext
 * @see AbstractPaginatedView
 */
public interface PaginatedViewContext<T> extends ViewContext, PaginatedVirtualView<T> {

    List<T> getSource();

    /**
     * Returns the current page.
     *
     * @return The current page of this context.
     */
    int getPage();

    /**
     * Returns the number of pages available in that context.
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
     * Returns <code>false</code> if the current page is the first page of the available pages or
     * <code>false</code> otherwise.
     *
     * @return If the current page is the first page of the available pages.
     */
    boolean hasPreviousPage();

    /**
     * Returns <code>false</code> if there are more pages than the current one available or <code>
     * false</code> otherwise.
     *
     * @return If there are more pages than the current one available.
     */
    boolean hasNextPage();

    /**
     * Returns `true` if the current page is the first page of the available pages or `false`
     * otherwise.
     */
    boolean isFirstPage();

    /**
     * Returns `true` if the current page is the last page of the available pages or `false`
     * otherwise.
     */
    boolean isLastPage();

    /**
     * Updates the current context by jumping to the specified page.
     *
     * @param page the new page.
     */
    void switchTo(@Range(from = 0, to = Integer.MAX_VALUE) int page);

    /** Updates the current context by switching to the previous page if available. */
    boolean switchToPreviousPage();

    /** Updates the current context by switching to the next page if available. */
    boolean switchToNextPage();

    @ApiStatus.Internal
    int getPreviousPageItemSlot();

    @ApiStatus.Internal
    void setPreviousPageItemSlot(int previousPageItemSlot);

    @ApiStatus.Internal
    int getNextPageItemSlot();

    @ApiStatus.Internal
    void setNextPageItemSlot(int nextPageItemSlot);

    @ApiStatus.Internal
    boolean isLayoutSignatureChecked();

    @ApiStatus.Internal
    void setLayoutSignatureChecked(boolean layoutSignatureChecked);

    @Override
    AbstractPaginatedView<T> getRoot();
}
