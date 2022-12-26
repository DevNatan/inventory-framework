package me.saiintbrisson.minecraft;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.ToString;
import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.IFSlotContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.pagination.IFPaginatedContext;
import me.devnatan.inventoryframework.pagination.IFPaginatedSlotContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PaginatedViewSlotContext implementation that inherits a ViewSlotContext for Bukkit platform.
 *
 * @param <T> The pagination item type.
 * @see IFSlotContext
 * @see IFPaginatedSlotContext
 */
@Getter
@ToString
final class BukkitPaginatedViewSlotContextImpl<T> extends ViewSlotContext implements IFPaginatedSlotContext<T> {

    private final int index;
    private final T value;

    BukkitPaginatedViewSlotContextImpl(
            int index,
            T value,
            int slot,
            ViewItem backingItem,
            IFContext parent,
            ViewContainer container,
            Player player) {
        super(slot, backingItem, parent, container, player);
        this.index = index;
        this.value = value;
    }

    @Override
    public long getIndex() {
        return getIndexOnCurrentPage() + ((long) getPageSize() * getPage());
    }

    @Override
    public int getIndexOnCurrentPage() {
        return index;
    }

    @Override
    public @NotNull IFPaginatedSlotContext<T> withItem(@Nullable Object item) {
        super.withItem(item);
        return this;
    }

    @Override
    public void setSource(@NotNull List<? extends T> source) {
        throwNotAllowedCall();
    }

    @Override
    public void setSource(@NotNull Function<IFPaginatedContext<T>, List<? extends T>> sourceProvider) {
        throwNotAllowedCall();
    }

    @Override
    public AsyncPaginationDataState<T> setSourceAsync(
            @NotNull Function<IFPaginatedContext<T>, CompletableFuture<List<? extends T>>> sourceFuture) {
        throwNotAllowedCall();
        return null;
    }

    @Override
    public void setPagesCount(int pagesCount) {
        throwNotAllowedCall();
    }

    @Override
    public void setLayout(String... layout) {
        throwNotAllowedCall();
    }

    @Override
    public void setLayout(char character, Supplier<ViewItem> factory) {
        throwNotAllowedCall();
    }

    @Override
    public void setLayout(char character, Consumer<ViewItem> factory) {
        throwNotAllowedCall();
    }

    @Override
    public Paginator<T> getPaginator() {
        return getParent().getPaginator();
    }

    @Override
    public int getPageSize() {
        return getParent().getPageSize();
    }

    @Override
    @Deprecated
    public int getPageMaxItemsCount() {
        return getParent().getPageMaxItemsCount();
    }

    @Override
    public @NotNull List<T> getSource() {
        return getParent().getSource();
    }

    @Override
    public int getPage() {
        return getParent().getPage();
    }

    @Override
    public void setPage(int page) {
        getParent().setPage(page);
    }

    @Override
    public int getPagesCount() {
        return getParent().getPagesCount();
    }

    @Override
    public int getPreviousPage() {
        return getParent().getPreviousPage();
    }

    @Override
    public int getNextPage() {
        return getParent().getNextPage();
    }

    @Override
    public boolean hasPreviousPage() {
        return getParent().hasPreviousPage();
    }

    @Override
    public boolean hasNextPage() {
        return getParent().hasNextPage();
    }

    @Override
    public boolean isFirstPage() {
        return getParent().isFirstPage();
    }

    @Override
    public boolean isLastPage() {
        return getParent().isLastPage();
    }

    @Override
    public void switchTo(int page) {
        getParent().switchTo(page);
    }

    @Override
    public boolean switchToPreviousPage() {
        return getParent().switchToPreviousPage();
    }

    @Override
    public boolean switchToNextPage() {
        return getParent().switchToNextPage();
    }

    @Override
    public int getPreviousPageItemSlot() {
        return getParent().getPreviousPageItemSlot();
    }

    @Override
    public void setPreviousPageItemSlot(int previousPageItemSlot) {
        throwNotAllowedCall();
    }

    @Override
    public int getNextPageItemSlot() {
        return getParent().getNextPageItemSlot();
    }

    @Override
    public void setNextPageItemSlot(int nextPageItemSlot) {
        throwNotAllowedCall();
    }

    @Override
    public boolean isLayoutSignatureChecked() {
        return getParent().isLayoutSignatureChecked();
    }

    @Override
    public void setLayoutSignatureChecked(boolean layoutSignatureChecked) {
        throwNotAllowedCall();
    }

    @Override
    public BiConsumer<IFPaginatedContext<T>, ViewItem> getPreviousPageItemFactory() {
        return getParent().getPreviousPageItemFactory();
    }

    @Override
    public BiConsumer<IFPaginatedContext<T>, ViewItem> getNextPageItemFactory() {
        return getParent().getNextPageItemFactory();
    }

    @Override
    public @NotNull AbstractPaginatedView<T> getRoot() {
        return getParent().getRoot();
    }

    @Override
    public void setPreviousPageItem(@NotNull BiConsumer<IFPaginatedContext<T>, ViewItem> previousPageItemFactory) {
        throwNotAllowedCall();
    }

    @Override
    public void setNextPageItem(@NotNull BiConsumer<IFPaginatedContext<T>, ViewItem> nextPageItemFactory) {
        throwNotAllowedCall();
    }

    @SuppressWarnings("unchecked")
    @Override
    public IFPaginatedSlotContext<T> paginated() {
        return this;
    }

    @Override
    public IFPaginatedContext<T> getParent() {
        return super.getParent().paginated();
    }

    @Override
    public void inventoryModificationTriggered() {
        throw new IllegalStateException(
                "Direct container modifications are not allowed from a paginated context because "
                        + "rendering a paginated item is an extensive method and can cause cyclic"
                        + " rendering on update, when rendering a paginated view.");
    }
}
