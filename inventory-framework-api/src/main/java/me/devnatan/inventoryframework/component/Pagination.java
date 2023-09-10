package me.devnatan.inventoryframework.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
     * Lazy pagination usually have a Function or Supplier as source provider and this provider
     * is only called again to set the current internal source when an explicit update is called.
     * <p>
     * So, when this method returns <code>true</code> the only way to update the current source as a
     * whole is triggering an update somehow e.g. by calling {@link #update()}.
     * <p>
     * Page switches will not trigger the source provider to re-apply the current internal source.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    boolean isLazy();

    /**
     * Static pagination usually have a Collection or something else as source provider and this
	 * source provider is called only on first render to set the current source as a whole,
	 * and never called again on the entire Pagination lifecycle.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    boolean isStatic();

    /**
     * If the pagination data is being loaded or not.
     * <p>
     * Only changes if {@link #isLazy()} is true.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    boolean isLoading();

    /**
     * Gets all elements in a given page index based of the specified source.
     *
     * @param index The page index.
     * @param pageSize Number of elements that each page can have.
     * @param pagesCount Pre-calculated total number of pages available (set zero if not available).
     * @param src   The source to split.
     * @return All elements in a page.
     * @throws IndexOutOfBoundsException If the specified index is {@code < 0} or
     *                                   exceeds the pages count.
     */
    static List<?> splitSourceForPage(int index, int pageSize, int pagesCount, List<?> src) {
        if (src.isEmpty()) return Collections.emptyList();

        if (src.size() <= pageSize) return new ArrayList<>(src);
        if (index < 0 || (pagesCount > 0 && index > pagesCount))
            throw new IndexOutOfBoundsException(String.format(
                    "Page index must be between the range of 0 and %d. Given: %d", pagesCount - 1, index));

        final List<Object> contents = new LinkedList<>();
        final int base = index * pageSize;
        int until = base + pageSize;
        if (until > src.size()) until = src.size();

        for (int i = base; i < until; i++) contents.add(src.get(i));

        return contents;
    }
}
