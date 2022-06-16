package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ToString(callSuper = true)
class BasePaginatedViewContext<T> extends BaseViewContext implements PaginatedViewContext<T> {

	@Setter(AccessLevel.PACKAGE)
	private int page;

	BasePaginatedViewContext(@NotNull AbstractView root, @Nullable ViewContainer container) {
		super(root, container);
	}

	@Override
	public final int getPage() {
		return page;
	}

	@Override
	public final int getPagesCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final int getPreviousPage() {
		return Math.max(0, page - 1);
	}

	@Override
	public final int getNextPage() {
		return Math.max(getPagesCount(), page + 1);
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
		return getPage() == getPagesCount();
	}

	@Override
	public final void switchTo(int page) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void switchToPreviousPage() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public final void switchToNextPage() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public final int getPageItemsCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void setSource(@NotNull List<T> source) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public final PaginatedViewSlotContext<T> ref(String key) {
		return super.ref(key).paginated();
	}

}
