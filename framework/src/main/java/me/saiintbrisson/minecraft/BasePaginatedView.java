package me.saiintbrisson.minecraft;

import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@ToString(callSuper = true)
class BasePaginatedView<T> extends AbstractView implements PaginatedVirtualView<T> {

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

}
