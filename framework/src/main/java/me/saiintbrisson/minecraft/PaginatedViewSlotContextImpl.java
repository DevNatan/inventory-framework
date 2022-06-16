package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;

/**
 * PaginatedViewSlotContext implementation that inherits a ViewSlotContext.
 *
 * @param <T> The pagination item type.
 * @see ViewSlotContext
 * @see PaginatedViewSlotContext
 */
@Getter
@ToString
final class PaginatedViewSlotContextImpl<T> extends AbstractViewSlotContext
	implements PaginatedViewSlotContext<T> {

	private final int index;
	private final T value;

	private final PaginatedViewContext<T> parent;

	PaginatedViewSlotContextImpl(
		int index,
		@NotNull T value,
		ViewItem backingItem,
		PaginatedViewContext<T> parent
	) {
		super(backingItem, (BaseViewContext) parent);
		this.index = index;
		this.value = value;
		this.parent = parent;
	}

	@Override
	void inventoryModificationTriggered() {
		throw new IllegalStateException(
			"Direct container modifications are not allowed from a paginated context because " +
				"rendering a paginated item is an extensive method and can cause cyclic" +
				" rendering on update, when rendering a paginated view."
		);
	}

	@Override
	public void setSource(@NotNull List<T> source) {
		throw new IllegalStateException(
			"It is not possible to change pagination data in a paginated item rendering context."
		);
	}

	@Override
	public PaginatedViewSlotContext<T> withItem(@Nullable Object item) {
		super.withItem(item);
		return this;
	}

	@Override
	public PaginatedViewSlotContext<T> ref(String key) {
		return super.ref(key).paginated();
	}

	@Override
	public int getPageItemsCount() {
		return parent.getPageItemsCount();
	}

	@Override
	public int getPage() {
		return parent.getPage();
	}

	@Override
	public int getPagesCount() {
		return parent.getPagesCount();
	}

	@Override
	public @Range(from = 0, to = Integer.MAX_VALUE) int getPreviousPage() {
		return parent.getPreviousPage();
	}

	@Override
	public @Range(from = 1, to = Integer.MAX_VALUE) int getNextPage() {
		return parent.getNextPage();
	}

	@Override
	public boolean hasPreviousPage() {
		return parent.hasPreviousPage();
	}

	@Override
	public boolean hasNextPage() {
		return parent.hasNextPage();
	}

	@Override
	public void switchTo(int page) {
		parent.switchTo(page);
	}
}
