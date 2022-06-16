package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PaginatedViewSlotContext implementation that inherits a ViewSlotContext.
 *
 * @param <T> The pagination item type.
 * @see ViewSlotContext
 * @see PaginatedViewSlotContext
 */
@Getter
@ToString
class PaginatedViewSlotContextImpl<T> extends AbstractViewSlotContext
	implements PaginatedViewSlotContext<T> {

	private final int index;
	private final T value;

	@Delegate
	private final BasePaginatedViewContext<T> parent;

	PaginatedViewSlotContextImpl(
		int index,
		@NotNull T value,
		ViewItem backingItem,
		BasePaginatedViewContext<T> parent
	) {
		super(backingItem, parent);
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
	public final PaginatedViewSlotContext<T> withItem(@Nullable Object item) {
		super.withItem(item);
		return this;
	}

}
