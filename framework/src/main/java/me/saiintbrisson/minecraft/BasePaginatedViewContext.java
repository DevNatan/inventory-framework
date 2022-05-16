package me.saiintbrisson.minecraft;

import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ToString(callSuper = true)
class BasePaginatedViewContext<T> extends BaseViewContext implements PaginatedViewContext<T> {

	private int page;

	BasePaginatedViewContext(@NotNull AbstractView root, @Nullable ViewContainer container) {
		super(root, container);
	}

	BasePaginatedViewContext(@NotNull ViewContext context) {
		super(context);
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public int getPagesCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPreviousPage() {
		return Math.max(0, page - 1);
	}

	@Override
	public int getNextPage() {
		return Math.max(getPagesCount(), page + 1);
	}

	@Override
	public boolean hasPreviousPage() {
		return getPreviousPage() < getPage();
	}

	@Override
	public boolean hasNextPage() {
		return getNextPage() > getPage();
	}

	@Override
	public void switchTo(int page) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPageItemsCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSource(@NotNull List<T> source) {

	}

}
