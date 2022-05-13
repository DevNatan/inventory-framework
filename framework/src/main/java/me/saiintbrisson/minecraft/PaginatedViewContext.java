package me.saiintbrisson.minecraft;

public interface PaginatedViewContext<T> extends ViewContext, Paginated<T> {

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
	 * Returns the total maximum number of fixed elements that a page can contain.
	 *
	 * @return the items count.
	 */
	@Deprecated
	default int getPageSize() {
		return getPageItemsCount();
	}

	/**
	 * Returns the total maximum number of fixed elements that a page can contain.
	 *
	 * @return the items count.
	 */
	int getPageItemsCount();

	/**
	 * Returns the index of the previous page.
	 */
	int getPreviousPage();

	/**
	 * Returns the number of the next page.
	 */
	int getNextPage();

	/**
	 * Returns `false` if the current page is the first page of the available pages or `true` otherwise
	 */
	boolean hasPreviousPage();

	/**
	 * Returns `true` if there are more pages than the current one available or `false` otherwise.
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

}
