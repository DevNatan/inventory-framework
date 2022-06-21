package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@Setter
@ToString(callSuper = true)
class BasePaginatedViewContext<T> extends BaseViewContext implements PaginatedViewContext<T> {

	private final SharedPaginationProperties<T> properties = new SharedPaginationProperties<>(this);

	@Setter(AccessLevel.PACKAGE)
	private int page;

	private int previousPageItemSlot = -1;
	private int nextPageItemSlot = -1;
	private boolean layoutSignatureChecked;

	BasePaginatedViewContext(@NotNull AbstractView root, @Nullable ViewContainer container) {
		super(root, container);
	}

	@Override
	public final int getOffset() {
		return getProperties().getOffset();
	}

	@Override
	@Deprecated
	public final void setOffset(int offset) {
		getProperties().setOffset(offset);
	}

	@Override
	public final int getLimit() {
		return getProperties().getLimit();
	}

	@Override
	@Deprecated
	public final void setLimit(int limit) {
		getProperties().setLimit(limit);
	}

	@Override
	public final int getPage() {
		return page;
	}

	@Override
	public final int getPagesCount() {
		return getProperties().getPaginator().count();
	}

	@Override
	@Deprecated
	public final int getPageSize() {
		return getProperties().getPaginator().getPageSize();
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
		root.runCatching(this, () -> root.updatePage(this, page));
		root.onPageSwitch(this);
	}

	@Override
	public final boolean switchToPreviousPage() {
		if (isFirstPage())
			return false;

		switchTo(getPage() - 1);
		return true;
	}

	@Override
	public final boolean switchToNextPage() {
		if (isLastPage())
			return false;

		switchTo(getPage() + 1);
		return true;
	}

	@Override
	public final int getPageItemsCount() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public final List<T> getSource() {
		return Collections.unmodifiableList(getProperties().getPaginator().getSource());
	}

	@Override
	public final void setSource(@NotNull List<T> source) {
		getProperties().getPaginator().setSource(source);
	}

	@Override
	public final void setSource(@NotNull Function<PaginatedViewContext<T>, List<T>> sourceProvider) {
		if (getProperties().hasSource() && getProperties().getPaginator().isProvided())
			throw new IllegalStateException("Pagination source cannot be provided more than once");
	}

	@Override
	public final void setLayout(String... layout) {
		getProperties().setLayout(layout);

		// check if layout was already resolved, if it was, update it without waiting for a update
		if (isLayoutSignatureChecked())
			getRoot().<T>paginated().updateLayout(this, layout);
	}

	@Override
	public final void setLayout(char identifier, @NotNull Consumer<ViewItem> layout) {
		setLayout(identifier, () -> {
			final ViewItem item = new ViewItem();
			layout.accept(item);
			return item;
		});
	}

	@Override
	public final void setLayout(char identifier, @NotNull Supplier<ViewItem> layout) {
		getProperties().setLayout(identifier, layout);
	}

	@Override
	public final PaginatedViewSlotContext<T> ref(String key) {
		return super.ref(key).paginated();
	}

	@Override
	public final AbstractPaginatedView<T> getRoot() {
		return super.getRoot().paginated();
	}

}
