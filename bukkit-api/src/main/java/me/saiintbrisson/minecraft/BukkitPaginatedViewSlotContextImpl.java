package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * PaginatedViewSlotContext implementation that inherits a ViewSlotContext for Bukkit platform.
 *
 * @param <T> The pagination item type.
 * @see ViewSlotContext
 * @see PaginatedViewSlotContext
 */
@Getter
@ToString
final class BukkitPaginatedViewSlotContextImpl<T> extends AbstractViewSlotContext
	implements PaginatedViewSlotContext<T> {

	private final int index;
	private final T value;

	@Getter(AccessLevel.NONE)
	private final PaginatedViewContext<T> parent;

	BukkitPaginatedViewSlotContextImpl(
		int index, @NotNull T value, ViewItem backingItem, PaginatedViewContext<T> parent) {
		super(backingItem, parent);
		this.index = index;
		this.value = value;
		this.parent = parent;
	}

	@Override
	public void inventoryModificationTriggered() {
		throw new IllegalStateException(
			"Direct container modifications are not allowed from a paginated context because "
				+ "rendering a paginated item is an extensive method and can cause cyclic"
				+ " rendering on update, when rendering a paginated view.");
	}

	@Override
	public int getSlot() {
		return index;
	}

	@Override
	public @NotNull Player getPlayer() {
		return BukkitViewer.toPlayerOfContext(this);
	}

	@Override
	public @NotNull PaginatedViewSlotContext<T> withItem(@Nullable Object item) {
		super.withItem(item);
		return this;
	}

	@Override
	public void setSource(@NotNull List<? extends T> source) {
		throwPaginationDataChangedError();
	}

	@Override
	public void setSource(@NotNull Function<PaginatedViewContext<T>, List<? extends T>> sourceProvider) {
		throwPaginationDataChangedError();
	}

	@Override
	public AsyncPaginationDataState<T> setSourceAsync(
		@NotNull Function<PaginatedViewContext<T>, CompletableFuture<List<T>>> sourceFuture) {
		throwPaginationDataChangedError();
		return null;
	}

	@Override
	public void setPagesCount(int pagesCount) {
		throwPaginationDataChangedError();
	}

	@Override
	public void setLayout(String... layout) {
		throwPaginationDataChangedError();
	}

	@Override
	public void setLayout(char character, Supplier<ViewItem> factory) {
		throwPaginationDataChangedError();
	}

	@Override
	public void setLayout(char character, Consumer<ViewItem> factory) {
		throwPaginationDataChangedError();
	}

	@Override
	public Paginator<T> getPaginator() {
		return parent.getPaginator();
	}

	@Override
	@Deprecated
	public int getPageSize() {
		return parent.getPageSize();
	}

	@Override
	public int getPageMaxItemsCount() {
		return parent.getPageMaxItemsCount();
	}

	@Override
	public @NotNull List<T> getSource() {
		return parent.getSource();
	}

	@Override
	public int getPage() {
		return parent.getPage();
	}

	@Override
	public void setPage(int page) {
		parent.setPage(page);
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
	public boolean isFirstPage() {
		return parent.isFirstPage();
	}

	@Override
	public boolean isLastPage() {
		return parent.isLastPage();
	}

	@Override
	public void switchTo(int page) {
		parent.switchTo(page);
	}

	@Override
	public boolean switchToPreviousPage() {
		return parent.switchToPreviousPage();
	}

	@Override
	public boolean switchToNextPage() {
		return parent.switchToNextPage();
	}

	@Override
	public int getPreviousPageItemSlot() {
		return parent.getPreviousPageItemSlot();
	}

	@Override
	public void setPreviousPageItemSlot(int previousPageItemSlot) {
		throwPaginationDataChangedError();
	}

	@Override
	public int getNextPageItemSlot() {
		return parent.getNextPageItemSlot();
	}

	@Override
	public void setNextPageItemSlot(int nextPageItemSlot) {
		throwPaginationDataChangedError();
	}

	@Override
	public boolean isLayoutSignatureChecked() {
		return parent.isLayoutSignatureChecked();
	}

	@Override
	public void setLayoutSignatureChecked(boolean layoutSignatureChecked) {
		throwPaginationDataChangedError();
	}

	@Override
	public BiConsumer<PaginatedViewContext<T>, ViewItem> getPreviousPageItemFactory() {
		return parent.getPreviousPageItemFactory();
	}

	@Override
	public BiConsumer<PaginatedViewContext<T>, ViewItem> getNextPageItemFactory() {
		return parent.getNextPageItemFactory();
	}

	@Override
	public @NotNull AbstractPaginatedView<T> getRoot() {
		return parent.getRoot();
	}

	@Override
	public void setPreviousPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory) {
		throwPaginationDataChangedError();
	}

	@Override
	public void setNextPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> nextPageItemFactory) {
		throwPaginationDataChangedError();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaginatedViewSlotContext<T> paginated() {
		return this;
	}

	private void throwPaginationDataChangedError() {
		throw new IllegalStateException(
			"It is not possible to change pagination data in a paginated item rendering context.");
	}
}
