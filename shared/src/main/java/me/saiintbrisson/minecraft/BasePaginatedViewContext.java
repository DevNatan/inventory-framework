package me.saiintbrisson.minecraft;

import static me.saiintbrisson.minecraft.ViewItem.UNSET;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @Deprecated
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
        return usePaginator().hasPage(getPage() + 1);
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
        root.runCatching(this, () -> {
            setPage(page);
            update();
            root.onPageSwitch(this);
        });
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
        return paginator;
    }

    @Override
    public void setPreviousPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory) {
        throw new UnsupportedOperationException(String.format(
                "Navigation items cannot be set in context scope. Use %s on root constructor instead.",
                "#setPreviousPageItem(BiConsumer<PaginatedViewContext<T>, ViewItem>)"));
    }

    @Override
    public void setNextPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> nextPageItemFactory) {
        throw new UnsupportedOperationException(String.format(
                "Navigation items cannot be set in context scope. Use %s on root constructor instead.",
                "#setNextPageItem(BiConsumer<PaginatedViewContext<T>, ViewItem>)"));
    }

    @Override
    public final @NotNull List<T> getSource() {
        return Collections.unmodifiableList(usePaginator().getSource());
    }

    private void checkUniquePaginationSource() {
        if (getPaginator() != null)
            throw new IllegalStateException("Pagination source can only be set once. If you need dynamic source use "
                    + "pagination source provider or asynchronous pagination source instead.");
    }

    @Override
    public final void setSource(@NotNull List<? extends T> source) {
        checkUniquePaginationSource();
        setPaginator(new Paginator<>(source));
    }

    @Override
    @ApiStatus.Experimental
    public final void setSource(@NotNull Function<PaginatedViewContext<T>, List<? extends T>> sourceProvider) {
        checkUniquePaginationSource();
        setPaginator(new Paginator<>(sourceProvider));
    }

    @Override
    @ApiStatus.Experimental
    public final AsyncPaginationDataState<T> setSourceAsync(
            @NotNull Function<PaginatedViewContext<T>, CompletableFuture<List<? extends T>>> sourceFuture) {
        checkUniquePaginationSource();

        final AsyncPaginationDataState<T> state = new AsyncPaginationDataState<>(sourceFuture);
        setPaginator(new Paginator<>(state));
        return state;
    }

    @Override
    public final void setPagesCount(int pagesCount) {
        final Paginator<T> paginator = getPaginator();
        if (paginator == null)
            throw new IllegalStateException("Paginator must be initialized before set the source size.");

        paginator.setPagesCount(pagesCount);
    }

    @Override
    public final @NotNull AbstractPaginatedView<T> getRoot() {
        return super.getRoot().paginated();
    }

    private Paginator<T> usePaginator() {
        return getPaginator() == null ? getRoot().getPaginator() : getPaginator();
    }
}
