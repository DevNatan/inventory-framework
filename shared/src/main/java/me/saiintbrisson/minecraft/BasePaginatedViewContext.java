package me.saiintbrisson.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
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

    @Setter(AccessLevel.PACKAGE)
    private int page;

    private int previousPageItemSlot = -1;
    private int nextPageItemSlot = -1;

    private boolean layoutSignatureChecked;
    private String[] layout;
    private Paginator<T> paginator;
    private Stack<Integer> itemsLayer;
    private List<LayoutPattern> layoutPatterns;

    BasePaginatedViewContext(@NotNull AbstractView root, @Nullable ViewContainer container) {
        super(root, container);
    }

    @ApiStatus.Internal
    public final String[] getLayout() {
        return layout;
    }

    @Override
    public final int getPage() {
        return page;
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
        if (getItemsLayer() == null) throw new IllegalStateException("Layout not resolved");

        return getItemsLayer().size();
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
    public final List<LayoutPattern> getLayoutPatterns() {
        if (layoutPatterns != null) return layoutPatterns;
        return getRoot().getLayoutPatterns();
    }

    @Override
    public final @NotNull List<T> getSource() {
        return Collections.unmodifiableList(getPaginator().getSource());
    }

    @Override
    public final void setSource(@NotNull List<? extends T> source) {
        tryResolveLayout();
        setPaginator(
                new Paginator<>(
                        isLayoutSignatureChecked() ? getItemsLayer().size() : getPageSize(),
                        source));
    }

    @Override
    @ApiStatus.Experimental
    public final void setSource(
            @NotNull Function<PaginatedViewContext<T>, List<? extends T>> sourceProvider) {
        final Paginator<T> paginator = getPaginator();
        if (paginator != null && paginator.isProvided())
            throw new IllegalStateException("Pagination source cannot be provided more than once");

        tryResolveLayout();
        setPaginator(
                new Paginator<>(
                        isLayoutSignatureChecked() ? getItemsLayer().size() : getPageSize(),
                        sourceProvider));
    }

    @Override
    @ApiStatus.Experimental
    public final AsyncPaginationDataState<T> setSourceAsync(
            @NotNull Function<PaginatedViewContext<T>, CompletableFuture<List<T>>> sourceFuture) {
        tryResolveLayout();

        final AsyncPaginationDataState<T> state = new AsyncPaginationDataState<>(sourceFuture);
        setPaginator(
                new Paginator<>(
                        isLayoutSignatureChecked() ? getItemsLayer().size() : getPageSize(),
                        state));
        return state;
    }

    @Override
    public final void setPagesCount(int pagesCount) {
        final Paginator<T> paginator = getPaginator();
        if (paginator == null)
            throw new IllegalStateException(
                    "Paginator must be initialized before set the source size.");

        paginator.setPagesCount(pagesCount);
    }

    private void tryResolveLayout() {
        final AbstractPaginatedView<T> root = getRoot();
        final boolean isLayoutChecked = isLayoutSignatureChecked();
        final String[] layout = root.useLayout(this);

        // force layout resolving but do not render
        if (!isLayoutChecked && layout != null) root.resolveLayout(this, layout, false);
    }

    @Override
    public final void setLayout(String... layout) {
        this.layout = layout;

        // allow dynamic layout update
        if (isLayoutSignatureChecked()) getRoot().updateLayout(this, layout);
    }

    @Override
    public void setLayout(char character, Supplier<ViewItem> factory) {
        getRoot().checkReservedLayoutCharacter(character);
        if (layoutPatterns == null) layoutPatterns = new ArrayList<>();

        layoutPatterns.add(new LayoutPattern(character, factory));
    }

    @Override
    public void setLayout(char character, Consumer<ViewItem> factory) {
        setLayout(
                character,
                () -> {
                    final ViewItem item = new ViewItem();
                    factory.accept(item);
                    return item;
                });
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
