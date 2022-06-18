package me.saiintbrisson.minecraft;

import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@ToString(callSuper = true)
abstract class AbstractPaginatedView<T> extends AbstractView implements PaginatedVirtualView<T> {

	private List<T> source;

	private Function<PaginatedViewContext<T>, ViewItem> previousPageItemFactory, nextPageItemFactory;

	AbstractPaginatedView(int rows, String title, @NotNull ViewType type) {
		super(rows, title, type);
	}

	@Override
	final void render(@NotNull ViewContext context) {
		super.render(context);

		if (getType() != ViewType.CHEST)
			throw new IllegalStateException(String.format(
				"Pagination is not supported in \"%s\" view type: %s." +
				" Use chest type instead.",
				getType().getIdentifier(),
				getClass().getName()
			));

		if (!hasSource() && !context.paginated().hasSource())
			throw new IllegalStateException(
				"At least one pagination source must be set. " +
					"Use #setSource in the PaginatedView constructor or set just to a context" +
					" in the #onRender(...) function with \"render.paginated().setSource(...)\"."
			);
	}

	@Override
	public boolean hasSource() {
		return source != null;
	}

	public final void setSource(@NotNull List<T> source) {
		ensureNotInitialized();
		this.source = source;
	}

	@Override
	public final int getPageItemsCount() {
		throw new UnsupportedOperationException();
	}

	public final void setPreviousPageItem(@NotNull Function<PaginatedViewContext<T>, ViewItem> previousPageItemFactory) {
		ensureNotInitialized();
		this.previousPageItemFactory = previousPageItemFactory;
	}

	public final void setNextPageItem(@NotNull Function<PaginatedViewContext<T>, ViewItem> nextPageItemFactory) {
		ensureNotInitialized();
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
	 * <p>
	 * You can use: item.withItem(...);
	 *
	 * @param render The pagination item rendering context.
	 * @param item   The item that'll be displayed.
	 * @param value  The paginated value.
	 */
	protected abstract void onItemRender(
		@NotNull PaginatedViewSlotContext<T> render,
		@NotNull ViewItem item,
		@NotNull T value
	);

}
