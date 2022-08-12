package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static me.saiintbrisson.minecraft.ViewItem.UNSET;

@Getter
@Setter
@ToString(callSuper = true)
class BasePaginatedViewContext<T> extends BaseViewContext implements PaginatedViewContext<T> {

	private int page;

	/* inherited from PaginatedVirtualView */
	private BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory, nextPageItemFactory;
	private Paginator<T> paginator;
	private int previousPageItemSlot = UNSET;
	private int nextPageItemSlot = UNSET;

	BasePaginatedViewContext(@NotNull AbstractView root, @Nullable ViewContainer container) {
		super(root, container);
	}

	@Override
	public final int getPage() {
		return page;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public final int getPagesCount() {
		return getPaginator().count();
	}

	@Override
	@Deprecated
	public final int getPageSize() {
		return getPaginator().getPageSize();
	}

	@Override
	public final int getPageMaxItemsCount() {
		if (getLayoutItemsLayer() == null) throw new IllegalStateException("Layout not resolved");

		return getLayoutItemsLayer().size();
	}

	@Override
	public final int getPreviousPage() {
		return Math.max(0, getPage() - 1);
	}

	@Override
	public final int getNextPage() {
		return Math.min(getPagesCount(), getPage() + 1);
	}

	@Override
	public final boolean hasPreviousPage() {
		return getPreviousPage() < getPage();
	}

	@Override
	public final boolean hasNextPage() {
		return getNextPage() > getPage();
	}

	@Override
	public final boolean isFirstPage() {
		return getPage() == 0;
	}

	@Override
	public final boolean isLastPage() {
		return !hasNextPage();
	}

	@Override
	public final void switchTo(final int page) {
		final AbstractPaginatedView<T> root = getRoot().paginated();
		root.runCatching(this, () -> root.updateContext(this, page, true, true));
		root.onPageSwitch(this);
	}

	@Override
	public final boolean switchToPreviousPage() {
		if (isFirstPage()) return false;

		switchTo(getPage() - 1);
		return true;
	}

	@Override
	public final boolean switchToNextPage() {
		if (isLastPage()) return false;

		switchTo(getPage() + 1);
		return true;
	}

	@ApiStatus.Internal
	public final Paginator<T> getPaginator() {
		if (paginator != null) return paginator;

		return getRoot().getPaginator();
	}

	@Override
	public void setPreviousPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory) {
		throw new UnsupportedOperationException("Navigation items cannot be set in context scope");
	}

	@Override
	public void setNextPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> nextPageItemFactory) {
		throw new UnsupportedOperationException("Navigation items cannot be set in context scope");
	}

	@Override
	public final List<LayoutPattern> getLayoutPatterns() {
		if (super.getLayoutPatterns() != null) return super.getLayoutPatterns();
		return getRoot().getLayoutPatterns();
	}

	@Override
	public final @NotNull List<T> getSource() {
		return Collections.unmodifiableList(getPaginator().getSource());
	}

	@Override
	public final void setSource(@NotNull List<? extends T> source) {
		tryResolveLayout();
		setPaginator(new Paginator<>(getLayoutItemsLayer().size(), source));
	}

	@Override
	@ApiStatus.Experimental
	public final void setSource(@NotNull Function<PaginatedViewContext<T>, List<? extends T>> sourceProvider) {
		final Paginator<T> paginator = getPaginator();
		if (paginator != null && paginator.isProvided())
			throw new IllegalStateException("Pagination source cannot be provided more than once");

		tryResolveLayout();
		setPaginator(new Paginator<>(getLayoutItemsLayer().size(), sourceProvider));
	}

	@Override
	@ApiStatus.Experimental
	public final AsyncPaginationDataState<T> setSourceAsync(
		@NotNull Function<PaginatedViewContext<T>, CompletableFuture<List<T>>> sourceFuture) {
		tryResolveLayout();

		final AsyncPaginationDataState<T> state = new AsyncPaginationDataState<>(sourceFuture);
		setPaginator(new Paginator<>(getLayoutItemsLayer().size(), state));
		return state;
	}

	@Override
	public final void setPagesCount(int pagesCount) {
		final Paginator<T> paginator = getPaginator();
		if (paginator == null)
			throw new IllegalStateException("Paginator must be initialized before set the source size.");

		paginator.setPagesCount(pagesCount);
	}

	private void tryResolveLayout() {
		final AbstractPaginatedView<T> root = getRoot();
		if (isLayoutSignatureChecked()) return;

		final String[] layout = root.useLayout(this);
		if (layout == null) return;

		// TODO
//		resolveLayout(this, this, layout);
	}

	@Override
	public final void setLayout(String... layout) {
		super.setLayout(layout);

		// allow dynamic layout update
		if (isLayoutSignatureChecked()) getRoot().updateLayout(this, layout);
	}

	@Override
	public final @NotNull AbstractPaginatedView<T> getRoot() {
		return super.getRoot().paginated();
	}

	@Override
	final int convertSlot(int row, int column) {
		return convertSlot(
			row,
			column,
			getContainer().getType().getRows(),
			getContainer().getType().getColumns());
	}


}
