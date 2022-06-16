package me.saiintbrisson.minecraft;

import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@ToString(callSuper = true)
abstract class BasePaginatedView<T> extends AbstractView implements PaginatedVirtualView<T> {

	@Setter
	private List<T> source;

	private Function<PaginatedViewContext<T>, ViewItem> previousPageItemFactory, nextPageItemFactory;

	BasePaginatedView(int rows, String title, @NotNull ViewType type) {
		super(rows, title, type);
	}

	@Override
	public final int getPageItemsCount() {
		throw new UnsupportedOperationException();
	}

	public final void setPreviousPageItem(@NotNull Function<PaginatedViewContext<T>, ViewItem> previousPageItemFactory) {
		this.previousPageItemFactory = previousPageItemFactory;
	}

	public final void setNextPageItem(@NotNull Function<PaginatedViewContext<T>, ViewItem> nextPageItemFactory) {
		this.nextPageItemFactory = nextPageItemFactory;
	}

	/**
	 * @deprecated Use {@link #setPreviousPageItem(Function)} on constructor instead.
	 */
	@Deprecated
	protected ViewItem getPreviousPageItem(@NotNull PaginatedViewContext<T> context) {
		return null;
	}

	/**
	 * @deprecated Use {@link #setNextPageItem(Function)} on constructor instead.
	 */
	@Deprecated
	protected ViewItem getNextPageItem(@NotNull PaginatedViewContext<T> context) {
		return null;
	}

	/**
	 * TODO document it properly
	 *
	 * You can use: item.withItem(...);
	 *
	 * @param render The pagination item rendering context.
	 * @param item The item that'll be displayed.
	 * @param value The paginated value.
	 */
	protected abstract void onItemRender(
		@NotNull PaginatedViewSlotContext<T> render,
		@NotNull ViewItem item,
		@NotNull T value
	);

}
