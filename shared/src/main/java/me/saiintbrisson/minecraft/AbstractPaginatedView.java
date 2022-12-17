package me.saiintbrisson.minecraft;

import static me.saiintbrisson.minecraft.ViewItem.UNSET;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.pagination.IFPaginatedContext;
import me.devnatan.inventoryframework.pagination.IFPaginatedSlotContext;
import me.saiintbrisson.minecraft.exception.InitializationException;
import me.saiintbrisson.minecraft.pipeline.Pipeline;
import me.saiintbrisson.minecraft.pipeline.interceptors.AvailableSlotRenderInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.LayoutPatternApplierInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.LayoutResolutionInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.NavigationControllerInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.OpenInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.PaginationRenderInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.RenderInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.ScheduledUpdateInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.UpdateInterceptor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString(callSuper = true)
@ApiStatus.NonExtendable
public abstract class AbstractPaginatedView<T> extends AbstractView implements PaginatedVirtualView<T> {

    private int offset, limit;

    /* inherited from PaginatedVirtualView */
    private BiConsumer<IFPaginatedContext<T>, ViewItem> previousPageItemFactory, nextPageItemFactory;
    private Paginator<T> paginator;

    private int previousPageItemSlot = UNSET;
    private int nextPageItemSlot = UNSET;

    AbstractPaginatedView() {
        // TODO apply values :)
    }

    AbstractPaginatedView(int rows, String title, @NotNull ViewType type) {
        super(rows, title, type);
        this.offset = 0;
        this.limit = getItems().length - 1;
    }

    /**
     * Called when a single pagination data is about to be rendered in the container.
     *
     * <p>The {@link ViewItem item parameter} is mutable and must be used to determine what will be
     * rendered in that context for that specific item.
     *
     * <pre><code>
     * &#64;Override
     * protected void onItemRender(
     *     PaginatedViewSlotContext&#60;T&#62; context,
     *     ViewItem viewItem,
     *     T value
     * ) {
     *     viewItem.withItem(platformStack).onClick(click -&#62; {
     *         // clicked on value
     *     });
     * }
     * </code></pre>
     *
     * <p>This function is called extensively, every time a paginated item is rendered or updated.
     *
     * <p>It is not allowed to call methods that {@link #inventoryModificationTriggered() trigger
     * modifications in the container} of the context or in the view within this rendering function,
     * and it is also not possible to use the item {@link ViewItem#onRender(ViewItemHandler) render}
     * and {@link ViewItem#onUpdate(ViewItemHandler) update} functions within this function since it
     * is already a rendering function of item itself.
     *
     * @param context  The pagination item rendering context.
     * @param viewItem A mutable instance of item that will be rendered.
     * @param value    The paginated value.
     */
    protected abstract void onItemRender(
            @NotNull IFPaginatedSlotContext<T> context, @NotNull ViewItem viewItem, @NotNull T value);

    /**
     * Called when pagination is switched.
     *
     * <p>The context in the parameter is the new paging context, so trying to {@link
     * IFPaginatedContext#getPage() get the current page} will return the new page.
     *
     * @param context The page switch context.
     */
    protected void onPageSwitch(@NotNull IFPaginatedContext<T> context) {}

    /**
     * Where pagination will start.
     *
     * @return The first slot that pagination can reach in the container.
     * @deprecated Offset and limit will be replaced by layout.
     */
    @Deprecated
    public final int getOffset() {
        return offset;
    }

    /**
     * Defines the slot that pagination will start in the container.
     *
     * @param offset Where pagination will start.
     * @throws InitializationException If this view is initialized.
     * @deprecated Offset and limit will be replaced by layout.
     */
    @Deprecated
    public final void setOffset(int offset) {
        ensureNotInitialized();
        this.offset = offset;
    }

    /**
     * Where pagination will end.
     *
     * @return The last slot that pagination can reach in the container.
     * @deprecated Offset and limit will be replaced by layout.
     */
    @Deprecated
    public final int getLimit() {
        return limit;
    }

    /**
     * Defines the last slot that pagination can reach in the container.
     *
     * @param limit Where pagination will end.
     * @throws InitializationException If this view is initialized.
     * @deprecated Offset and limit will be replaced by layout.
     */
    @Deprecated
    public final void setLimit(int limit) {
        ensureNotInitialized();
        this.limit = limit;
    }

    /**
     * {@inheritDoc}
     */
    @ApiStatus.Internal
    public final Paginator<T> getPaginator() {
        return paginator;
    }

    /**
     * {@inheritDoc}
     */
    @ApiStatus.Internal
    public final BiConsumer<IFPaginatedContext<T>, ViewItem> getPreviousPageItemFactory() {
        return previousPageItemFactory;
    }

    /**
     * {@inheritDoc}
     */
    @ApiStatus.Internal
    public final BiConsumer<IFPaginatedContext<T>, ViewItem> getNextPageItemFactory() {
        return nextPageItemFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public final int getPreviousPageItemSlot() {
        return previousPageItemSlot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public final void setPreviousPageItemSlot(int previousPageItemSlot) {
        this.previousPageItemSlot = previousPageItemSlot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public final int getNextPageItemSlot() {
        return nextPageItemSlot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public final void setNextPageItemSlot(int nextPageItemSlot) {
        this.nextPageItemSlot = nextPageItemSlot;
    }

    /**
     * The item that will be used to represent the backward navigation item.
     *
     * <p>Sample code:
     *
     * <pre><code>
     * &#64;Override
     * protected ViewItem getPreviousPageItem(PaginatedViewContext&#60;T&#62; context) {
     *     return item(fallbackItem);
     * }
     * </code></pre>
     *
     * @param context The pagination context.
     * @return The backward navigation item.
     * @deprecated Use {@link #setPreviousPageItem(BiConsumer)} on constructor instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    public ViewItem getPreviousPageItem(@NotNull IFPaginatedContext<T> context) {
        return null;
    }

    /**
     * The item that will be used to represent the forward navigation item.
     *
     * <p>Sample code:
     *
     * <pre><code>
     * &#64;Override
     * protected ViewItem getNextPageItem(PaginatedViewContext&#60;T&#62; context) {
     *     return item(fallbackItem);
     * }
     * </code></pre>
     *
     * @param context The pagination context.
     * @return The forward navigation item.
     * @deprecated Use {@link #setNextPageItem(BiConsumer)} on constructor instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    public ViewItem getNextPageItem(@NotNull IFPaginatedContext<T> context) {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final void setPreviousPageItem(
            @NotNull BiConsumer<IFPaginatedContext<T>, ViewItem> previousPageItemFactory) {
        ensureNotInitialized();
        this.previousPageItemFactory = previousPageItemFactory;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final void setNextPageItem(@NotNull BiConsumer<IFPaginatedContext<T>, ViewItem> nextPageItemFactory) {
        ensureNotInitialized();
        this.nextPageItemFactory = nextPageItemFactory;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final void setSource(@NotNull List<? extends T> source) {
        ensureNotInitialized();
        this.paginator = new Paginator<>(getExpectedPageSize(), source);
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    @ApiStatus.Experimental
    public final void setSource(@NotNull Function<IFPaginatedContext<T>, List<? extends T>> sourceProvider) {
        ensureNotInitialized();
        this.paginator = new Paginator<>(getExpectedPageSize(), sourceProvider);
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    @ApiStatus.Experimental
    public final AsyncPaginationDataState<T> setSourceAsync(
            @NotNull Function<IFPaginatedContext<T>, CompletableFuture<List<? extends T>>> sourceFuture) {
        ensureNotInitialized();
        final AsyncPaginationDataState<T> state = new AsyncPaginationDataState<>(sourceFuture);
        this.paginator = new Paginator<>(getExpectedPageSize(), state);
        return state;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    @ApiStatus.Experimental
    public final void setPagesCount(int pagesCount) {
        ensureNotInitialized();

        if (getPaginator() == null)
            throw new IllegalStateException("Paginator must be initialized before set the source size.");

        getPaginator().setPagesCount(pagesCount);
    }

    private int getExpectedPageSize() {
        return limit - offset;
    }

    @ApiStatus.Internal
    public void callItemRender(
            @NotNull IFPaginatedSlotContext<T> context, @NotNull ViewItem viewItem, @NotNull T value) {
        onItemRender(context, viewItem, value);
    }

    @Override
    @ApiStatus.OverrideOnly
    void beforeInit() {
        final Pipeline<VirtualView> pipeline = getPipeline();
        pipeline.intercept(OPEN, new OpenInterceptor());
        pipeline.intercept(INIT, new LayoutResolutionInterceptor());
        pipeline.intercept(INIT, new LayoutPatternApplierInterceptor());
        pipeline.intercept(RENDER, new LayoutResolutionInterceptor() /* context scope */);
        pipeline.intercept(RENDER, new PaginationRenderInterceptor());
        pipeline.intercept(RENDER, new LayoutPatternApplierInterceptor() /* context scope */);
        pipeline.intercept(RENDER, new AvailableSlotRenderInterceptor());
        pipeline.intercept(RENDER, new RenderInterceptor());
        pipeline.intercept(RENDER, new NavigationControllerInterceptor());
        pipeline.intercept(RENDER, new ScheduledUpdateInterceptor.Render());
        pipeline.intercept(UPDATE, new UpdateInterceptor());
        pipeline.intercept(UPDATE, new PaginationRenderInterceptor());
        pipeline.intercept(UPDATE, new NavigationControllerInterceptor());
        pipeline.intercept(CLOSE, new ScheduledUpdateInterceptor.Close());
    }
}
