package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PaginatedVirtualView<T> extends VirtualView {

	int getOffset();

	/**
	 * @deprecated Offset and limit will be replaced by layout.
	 * Use {@link #setLayout(String...)} instead.
	 */
	@Deprecated
	void setOffset(int offset);

	int getLimit();

	/**
	 * Defines the last slot that paging data can reach in the container.
	 *
	 * @deprecated Offset and limit will be replaced by layout.
	 * Use {@link #setLayout(String...)} instead.
	 */
	@Deprecated
	void setLimit(int limit);

	/**
	 * Returns the total maximum number of fixed elements that a page can contain.
	 *
	 * @return The items count that a single page can contain.
	 * @deprecated Use {@link #getPageItemsCount()} instead.
	 */
	@Deprecated
	int getPageSize();

	/**
	 * Returns the total maximum number of fixed elements that a page can contain.
	 *
	 * @return The items count that a single page can contain.
	 */
	int getPageItemsCount();

	/**
	 * Defines the data that will be used to populate this paginated view.
	 * <p>
	 * Note that this is a "static operation", your data will not be updated automatically since it
	 * is not a provider, that is, during the view update the paginated items can be reordered,
	 * but they will not be repaved, for that, you need to specify a
	 * {@link #setSource(Function) pagination provider} that will make your pagination dynamic.
	 * <p>
	 * You can call this method as many times as you like followed by {@link #update()}
	 * to update the container with the new items.
	 *
	 * @param source The pagination data source.
	 */
	void setSource(@NotNull List<T> source);

	/**
	 * Defines the data provider that will be used to populate this paginated view.
	 * <p>
	 * Provider's {@link PaginatedViewContext first parameter} can be used to determine which
	 * is {@link PaginatedViewContext#getPage() the current page} of the context, use to sub-split
	 * your request data and return the correct data for each page.
	 * <p>
	 * Optimize your requests by specifying a limit of data to be returned, this limit being the
	 * {@link PaginatedView#getPageItemsCount() maximum amount of items that the container can support},
	 * taking into account paging nuances, such as {@link SharedPaginationProperties#getLayout() pagination layout}.
	 * <p>
	 * You can only use this method once during the view's lifecycle as it is a provider,
	 * and the pagination resets with each update accordingly.
	 * <p>
	 * <i>This is an experimental api which is subject to change.</i>
	 *
	 * @param sourceProvider The pagination data source provider.
	 */
	@ApiStatus.Experimental
	void setSource(@NotNull Function<PaginatedViewContext<T>, List<T>> sourceProvider);

	void setLayout(String... layout);

	/**
	 * @deprecated Use {@link #setLayout(char, Consumer)} instead.
	 */
	@Deprecated
	void setLayout(char identifier, @NotNull Supplier<ViewItem> layout);

	@ApiStatus.Experimental
	void setLayout(char identifier, @NotNull Consumer<ViewItem> layout);

	/**
	 * Pagination data used for this view.
	 * <p>
	 * <i>This is an internal API, please don't rely on it.</i>
	 *
	 * @return Pagination properties for this view.
	 */
	@ApiStatus.Internal
	SharedPaginationProperties<T> getProperties();

}
