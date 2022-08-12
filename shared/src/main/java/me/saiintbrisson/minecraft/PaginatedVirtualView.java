package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.exception.InitializationException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PaginatedVirtualView<T> extends VirtualView {

	byte NAVIGATE_LEFT = 0;

	byte NAVIGATE_RIGHT = 1;

	/**
	 * Defines the data that will be used to populate this paginated view.
	 *
	 * <p>Note that this is a "static operation", your data will not be updated automatically since it
	 * is not a provider, that is, during the view update the paginated items can be reordered, but
	 * they will not be repaved, for that, you need to specify a {@link #setSource(Function)
	 * pagination provider} that will make your pagination dynamic.
	 *
	 * <p>You can call this method as many times as you like followed by {@link #update()} to update
	 * the container with the new items.
	 *
	 * @param source The pagination data source.
	 */
	void setSource(@NotNull List<? extends T> source);

	/**
	 * Defines the data provider that will be used to populate this paginated view.
	 *
	 * <p>Provider's {@link PaginatedViewContext first parameter} can be used to determine which is
	 * {@link PaginatedViewContext#getPage() the current page} of the context, use to sub-split your
	 * request data and return the correct data for each page.
	 *
	 * <p>Optimize your requests by specifying a limit of data to be returned, this limit being the
	 * {@link PaginatedViewContext#getPageMaxItemsCount()} maximum amount of items} that the container
	 * can support, taking into account paging nuances, such as {@link
	 * PaginatedVirtualView#getLayout()} pagination layout.
	 *
	 * <p>You can only use this method once during the view's lifecycle as it is a provider, and the
	 * pagination resets with each update accordingly.
	 *
	 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @param sourceProvider The pagination data source provider.
	 */
	@ApiStatus.Experimental
	void setSource(@NotNull Function<PaginatedViewContext<T>, List<? extends T>> sourceProvider);

	/**
	 * Asynchronously defines the data that will be used to populate this paginated view.
	 *
	 * <p>The CompletableFuture parameter must be used to provide the data that will be used for
	 * paging asynchronously. The pagination data will only be rendered after the job is finished.
	 *
	 * <p>Returns an {@link AsyncPaginationDataState} that can be used to manipulate the context
	 * according to the stage of loading this data.
	 *
	 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @param sourceFuture The pagination data source job.
	 * @return Asynchronous pagination data state to the specified source.
	 */
	@ApiStatus.Experimental
	AsyncPaginationDataState<T> setSourceAsync(
		@NotNull Function<PaginatedViewContext<T>, CompletableFuture<List<T>>> sourceFuture);

	/**
	 * Defines the amount of pages that will be available for pagination.
	 *
	 * <p>This amount will only be used for asynchronous or lazy pagination as these are cases where
	 * the data is provided in a partitioned way, so its total size of available pages cannot be
	 * determined.
	 *
	 * <p>The source size used internally to determine the amount of page available and to delimit
	 * what data from each page will be displayed.
	 *
	 * <p>Unable to set data size for synchronous paging, resulting in an exception.
	 *
	 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @param pagesCount The number of pages that'll be available to pagination.
	 */
	@ApiStatus.Experimental
	void setPagesCount(int pagesCount);

	/**
	 * The paginator of this view.
	 *
	 * <p>The value returned depends on the implementation, so other paginators besides the view
	 * itself can be obtained.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return The paginator of this view.
	 */
	@ApiStatus.Internal
	Paginator<T> getPaginator();

	/**
	 * The current position of the navigation's "previous page" item.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return The position of the navigation's "previous page" item, <code>-1</code> if unset.
	 */
	@ApiStatus.Internal
	int getPreviousPageItemSlot();

	/**
	 * Sets the navigation's "previous page" item position in this context.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param previousPageItemSlot The new navigation's "previous page" item position.
	 */
	@ApiStatus.Internal
	void setPreviousPageItemSlot(int previousPageItemSlot);

	/**
	 * The current position of the navigation's "next page" item.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return The position of the navigation's "next page" item, <code>-1</code> if unset.
	 */
	@ApiStatus.Internal
	int getNextPageItemSlot();

	/**
	 * Sets the navigation's "next page" item position in this context.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param nextPageItemSlot The new navigation's "next page" item position.
	 */
	@ApiStatus.Internal
	void setNextPageItemSlot(int nextPageItemSlot);

	/**
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 */
	@ApiStatus.Internal
	BiConsumer<PaginatedViewContext<T>, ViewItem> getPreviousPageItemFactory();

	/**
	 * The factory that will be used to create the "previous page" navigation item.
	 * <p>
	 * The first parameter is the current context and the second parameter is a mutable instance of
	 * {@link ViewItem} that will be used to determine the navigation item.
	 *
	 * <pre>{@code
	 * setPreviousPageItem((context, item) -> {
	 *     item.withItem(...);
	 * });
	 * }</pre>
	 * <p>
	 * You can hide the item if the context is on the first page by simply ignoring the factory
	 * <pre>{@code
	 * setPreviousPageItem((context, item) -> {
	 *     if (!context.hasPreviousPage())
	 *         return;
	 *
	 *      item.withItem(...);
	 * });
	 * }</pre>
	 * <p>
	 * or setting the fallback item to null.
	 * <pre>{@code
	 * setPreviousPageItem((context, item) -> {
	 *     item.withItem(context.hasPreviousPage() ? ... : null);
	 * });
	 * }</pre>
	 *
	 * @param previousPageItemFactory The navigation item factory.
	 * @throws InitializationException If this view is initialized.
	 */
	void setPreviousPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory);

	/**
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 */
	@ApiStatus.Internal
	BiConsumer<PaginatedViewContext<T>, ViewItem> getNextPageItemFactory();

	/**
	 * The factory that will be used to create the "next page" navigation item.
	 * <p>
	 * The first parameter is the current context and the second parameter is a mutable instance of
	 * {@link ViewItem} that will be used to determine the navigation item.
	 *
	 * <pre>{@code
	 * setNextPageItem((context, item) -> {
	 *     item.withItem(...);
	 * });
	 * }</pre>
	 * <p>
	 * You can hide the item if there are no more pages available by simply ignoring the factory
	 * <pre>{@code
	 * setNextPageItem((context, item) -> {
	 *     if (!context.hasNextPage())
	 *         return;
	 *
	 *      item.withItem(...);
	 * });
	 * }</pre>
	 * <p>
	 * or setting the fallback item to null.
	 * <pre>{@code
	 * setNextPageItem((context, item) -> {
	 *     item.withItem(context.hasNextPage() ? ... : null);
	 * });
	 * }</pre>
	 *
	 * @param nextPageItemFactory The navigation item factory.
	 * @throws InitializationException If this view is initialized.
	 */
	void setNextPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> nextPageItemFactory);

}
