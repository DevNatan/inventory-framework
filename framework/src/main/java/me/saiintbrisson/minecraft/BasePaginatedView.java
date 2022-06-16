package me.saiintbrisson.minecraft;

import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@ToString(callSuper = true)
abstract class BasePaginatedView<T> extends AbstractView implements PaginatedVirtualView<T> {

	private List<T> source;

	private Function<PaginatedViewContext<T>, ViewItem> previousPageItemFactory, nextPageItemFactory;

	BasePaginatedView(int rows, String title, @NotNull ViewType type) {
		super(rows, title, type);
	}

	@Override
	final void render(@NotNull ViewContext context) {
		super.render(context);

		if (!hasSource() && !context.paginated().hasSource())
			throw new IllegalStateException(
				"At least one pagination source must be set, " +
				"use #setSource in the PaginatedView constructor or set just to a context" +
				" in the #onRender(...) function with \"render.paginated().setSource(...)\"."
			);
	}

	public final void setSource(@NotNull List<T> source) {
		this.source = source;
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
