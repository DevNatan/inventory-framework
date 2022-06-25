package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface PaginatedVirtualView<T> extends VirtualView {

	char PREVIOUS_PAGE_CHAR = '<';
	char NEXT_PAGE_CHAR = '>';
	char EMPTY_SLOT_CHAR = 'X';
	char ITEM_SLOT_CHAR = 'O';

	/**
	 * Defines the data that will be used to populate this paginated view.
	 *
	 * <p>Note that this is a "static operation", your data will not be updated automatically since
	 * it is not a provider, that is, during the view update the paginated items can be reordered,
	 * but they will not be repaved, for that, you need to specify a {@link #setSource(Function)
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
	 * {@link PaginatedViewContext#getPageMaxItemsCount()} maximum amount of items} that the
	 * container can support, taking into account paging nuances, such as {@link
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
	 * <p>
	 * The CompletableFuture parameter must be used to provide the data that will be used for paging
	 * asynchronously. The pagination data will only be rendered after the job is finished.
	 * <p>
	 * Returns an {@link AsyncPaginationDataState} that can be used to manipulate the context
	 * according to the stage of loading this data.
	 *
	 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @param sourceFuture The pagination data source job.
	 */
	@ApiStatus.Experimental
	AsyncPaginationDataState<? extends T> setSource(@NotNull CompletableFuture<? extends T> sourceFuture);

	/**
	 * The layout defined for this view by the user.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return The layout defined for this view.
	 */
	@ApiStatus.Internal
	@Nullable
	String[] getLayout();

	void setLayout(@Nullable String... layout);

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
}
