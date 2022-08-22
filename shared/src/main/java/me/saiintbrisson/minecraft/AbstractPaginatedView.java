package me.saiintbrisson.minecraft;

import static me.saiintbrisson.minecraft.ViewItem.UNSET;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.ToString;
import me.saiintbrisson.minecraft.exception.InitializationException;
import me.saiintbrisson.minecraft.pipeline.PipelinePhase;
import me.saiintbrisson.minecraft.pipeline.interceptors.NavigationControllerInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.PaginationRenderInterceptor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@ToString(callSuper = true)
public abstract class AbstractPaginatedView<T> extends AbstractView implements PaginatedVirtualView<T> {

    static final PipelinePhase PAGE_SWITCH = new PipelinePhase("page-switch");

    private int offset, limit;

    /* inherited from PaginatedVirtualView */
    private BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory, nextPageItemFactory;
    private Paginator<T> paginator;

    private int previousPageItemSlot = UNSET;
    private int nextPageItemSlot = UNSET;

    AbstractPaginatedView(int rows, String title, @NotNull ViewType type) {
        super(rows, title, type);
        this.offset = 0;
        this.limit = getItems().length - 1;
    }

    @Override
    protected void onRender(@NotNull ViewContext context) {
        super.onRender(context);
    }

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
    public final void setOffset(int offset) throws InitializationException {
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
    public final void setLimit(int limit) throws InitializationException {
        ensureNotInitialized();
        this.limit = limit;
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
            @NotNull PaginatedViewSlotContext<T> context, @NotNull ViewItem viewItem, @NotNull T value);

    /**
     * Called when pagination is switched.
     *
     * <p>The context in the parameter is the new paging context, so trying to {@link
     * PaginatedViewContext#getPage() get the current page} will return the new page.
     *
     * @param context The page switch context.
     */
    protected void onPageSwitch(@NotNull PaginatedViewContext<T> context) {}

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
    public final BiConsumer<PaginatedViewContext<T>, ViewItem> getPreviousPageItemFactory() {
        return previousPageItemFactory;
    }

    /**
     * {@inheritDoc}
     */
    @ApiStatus.Internal
    public final BiConsumer<PaginatedViewContext<T>, ViewItem> getNextPageItemFactory() {
        return nextPageItemFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getPreviousPageItemSlot() {
        return previousPageItemSlot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setPreviousPageItemSlot(int previousPageItemSlot) {
        this.previousPageItemSlot = previousPageItemSlot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getNextPageItemSlot() {
        return nextPageItemSlot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setNextPageItemSlot(int nextPageItemSlot) {
        this.nextPageItemSlot = nextPageItemSlot;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public final void setLayout(@Nullable String... layout) {
        ensureNotInitialized();
        super.setLayout(layout);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public final void setLayout(char character, Supplier<ViewItem> factory) {
        ensureNotInitialized();
        super.setLayout(character, factory);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public final void setLayout(char identifier, @Nullable Consumer<ViewItem> layout) {
        ensureNotInitialized();
        super.setLayout(identifier, layout);
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
    public ViewItem getPreviousPageItem(@NotNull PaginatedViewContext<T> context) {
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
    public ViewItem getNextPageItem(@NotNull PaginatedViewContext<T> context) {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final void setPreviousPageItem(
            @NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory)
            throws InitializationException {
        ensureNotInitialized();
        this.previousPageItemFactory = previousPageItemFactory;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final void setNextPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> nextPageItemFactory)
            throws InitializationException {
        ensureNotInitialized();
        this.nextPageItemFactory = nextPageItemFactory;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final void setSource(@NotNull List<? extends T> source) throws InitializationException {
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
    public final void setSource(@NotNull Function<PaginatedViewContext<T>, List<? extends T>> sourceProvider)
            throws InitializationException {
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
            @NotNull Function<PaginatedViewContext<T>, CompletableFuture<List<T>>> sourceFuture)
            throws InitializationException {
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
    public void setPagesCount(int pagesCount) throws InitializationException {
        ensureNotInitialized();

        if (this.paginator == null)
            throw new IllegalStateException("Paginator must be initialized before set the source size.");

        this.paginator.setPagesCount(pagesCount);
    }

    private int getExpectedPageSize() {
        return limit - offset;
    }

    @ApiStatus.Internal
    public void callItemRender(
            @NotNull PaginatedViewSlotContext<T> context, @NotNull ViewItem viewItem, @NotNull T value) {
        onItemRender(context, viewItem, value);
    }

    @Override
    @ApiStatus.OverrideOnly
    protected void beforeInit() {
        super.beforeInit();
        getPipeline().intercept(RENDER, new PaginationRenderInterceptor());
        getPipeline().intercept(RENDER, new NavigationControllerInterceptor());
        getPipeline().intercept(UPDATE, new PaginationRenderInterceptor());
        getPipeline().intercept(UPDATE, new NavigationControllerInterceptor());
    }
}
