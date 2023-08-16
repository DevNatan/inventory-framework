package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateValueHost;
import org.jetbrains.annotations.ApiStatus;

/**
 * Pagination is a host to multiple components that can be paginated, essentially it is a {@link
 * me.devnatan.inventoryframework.component.ComponentComposition} divided into sections called
 * <i>pages</i>.
 *
 * <p>It's a "malformed" structure from a data rendering point of view, that is, the components from
 * its minimum slot position to its maximum slot position may or may not be part of your
 * composition.
 *
 * <p>This component has an internal state used to control which <i>page</i> will be displayed
 * during rendering, this component is determined from the {@link #currentPageIndex() current page
 * index}.
 */
public interface Pagination extends ComponentComposition, StateValue {

    /**
     * The current page number.
     *
     * @return The current page number. Returns {@code 1} if it's in the first page.
     */
    int currentPage();

    /**
     * The index of the current page.
     *
     * @return The index of the current page. Returns {@code 0} if it's in the first page.
     */
    int currentPageIndex();

    /**
     * The number of the next page.
     *
     * @return The number of the next page at least {@link #lastPage()}.
     */
    int nextPage();

    /**
     * The index of the next page.
     *
     * @return The index of the next page at least {@link #lastPageIndex()}.
     */
    int nextPageIndex();

    /**
     * The number of the last page.
     * <p>
     * This is a shortcut to {@link #lastPageIndex()} {@code + 1}.
     *
     * @return The number of the last page.
     */
    int lastPage();

    /**
     * The index of the last page.
     * <p>
     * Pages starts from {@code 0} so the last page should be displayed as {@code lastPage + 1}.
     *
     * @return The index of the last page.
     */
    int lastPageIndex();

    /**
     * Checks if the {@link #currentPage() current page} is the first page (at index 0).
     *
     * @return If the current page is the first page.
     */
    boolean isFirstPage();

    /**
     * Checks if the {@link #currentPage() current page} is the last page (at {@link #lastPage()}).
     *
     * @return If the current page is the first page.
     */
    boolean isLastPage();

    /**
     * Checks if a page exists.
     *
     * @param pageIndex The page index to check.
     * @return If exists a page with the specified index.
     */
    boolean hasPage(int pageIndex);

    /**
     * Switches to a specific page index.
     *
     * @param pageIndex The page index to switch to.
     * @throws IndexOutOfBoundsException If a page with the specified index is not found.
     */
    void switchTo(int pageIndex);

    /**
     * Advances to the next page if available.
     */
    void advance();

    /**
     * Checks for pages to advance.
     *
     * @return {@code true} if there are pages to advance or {@code false} otherwise.
     */
    boolean canAdvance();

    /**
     * Backs to the previous page if available.
     */
    void back();

    /**
     * Checks for pages to back.
     *
     * @return {@code true} if there are pages to back or {@code false} otherwise.
     */
    boolean canBack();

    /**
     * Layout target character that determines the boundary positions that this component should be
     * rendered. Used if there is more than one pagination component sharing the same {@link StateValueHost}.
     *
     * @return The layout character target if set or {@code null}.
     */
    char getLayoutTarget();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    boolean isDynamic();

    /**
     * If the pagination data is being loaded or not.
     * <p>
     * Only changes if {@link #isDynamic()} is true.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    boolean isLoading();
}
