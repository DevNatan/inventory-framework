package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.Range;

/**
 * A paging context can originate from a view that supports paging.
 * <p>
 * It contains unique properties for a context like page, page count, page change and others.
 * There is no difference between a context and a paginated context, just some new properties
 * but nothing that changes its nature, life cycle or way of working.
 * <p>
 * A context can be converted to a paginated context if its root is paginated.
 * See {@link ViewContext#paginated()}.
 *
 * @param <T> The paginated item type.
 * @see ViewContext
 * @see BasePaginatedView
 */
public interface PaginatedViewContext<T> extends ViewContext, PaginatedVirtualView<T> {

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
	 * @return The index of the previous page.
	 */
	@Range(from = 0, to = Integer.MAX_VALUE)
	int getPreviousPage();

	/**
	 * Returns the index of the next page.
	 * @return The index of the next page.
	 */
	@Range(from = 1, to = Integer.MAX_VALUE)
	int getNextPage();

	/**
	 * Returns <tt>false</tt> if the current page is the first page of the available pages or <tt>false</tt> otherwise.
	 * @return If the current page is the first page of the available pages.
	 */
	boolean hasPreviousPage();

	/**
	 * Returns <tt>false</tt> if there are more pages than the current one available or <tt>false</tt> otherwise.
	 * @return If there are more pages than the current one available.
	 */
	boolean hasNextPage();

	/**
	 * Returns `true` if the current page is the first page of the available pages or `false` otherwise.
	 */
	default boolean isFirstPage() {
		return !hasPreviousPage();
	}

	/**
	 * Returns `true` if the current page is the last page of the available pages or `false` otherwise.
	 */
	default boolean isLastPage() {
		return !hasNextPage();
	}

	/**
	 * Updates the current context by jumping to the specified page.
	 *
	 * @param page the new page.
	 */
	void switchTo(final int page);

	/**
	 * Updates the current context by switching to the previous page if available.
	 */
	default void switchToPreviousPage() {
		switchTo(getPage() - 1);
	}

	/**
	 * Updates the current context by switching to the next page if available.
	 */
	default void switchToNextPage() {
		switchTo(getPage() + 1);
	}

	@Override
	PaginatedViewSlotContext<T> ref(String key);

}
