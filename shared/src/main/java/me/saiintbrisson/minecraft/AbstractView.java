package me.saiintbrisson.minecraft;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractView extends AbstractVirtualView {

    @Getter(AccessLevel.NONE)
    static final PipelinePhase OPEN = new PipelinePhase("open");

    @Getter(AccessLevel.NONE)
    static final PipelinePhase RENDER = new PipelinePhase("render");

    @Getter(AccessLevel.NONE)
    static final PipelinePhase UPDATE = new PipelinePhase("update");

    @Getter(AccessLevel.NONE)
    static final PipelinePhase CLICK = new PipelinePhase("click");

    @Getter(AccessLevel.NONE)
    static final PipelinePhase CLOSE = new PipelinePhase("close");

    static final ViewType DEFAULT_TYPE = ViewType.CHEST;

    @ToString.Include private final int size;
    @ToString.Include private final String title;
    @ToString.Include private final @NotNull ViewType type;

    PlatformViewFrame<?, ?, ?> viewFrame;

    private final Set<ViewContext> contexts =
            Collections.newSetFromMap(Collections.synchronizedMap(new HashMap<>()));

    private final Pipeline<ViewContext> pipeline =
            new Pipeline<>(OPEN, RENDER, UPDATE, CLICK, CLOSE);

    @ToString.Include
    private boolean cancelOnClick,
            cancelOnPickup,
            cancelOnDrop,
            cancelOnDrag,
            cancelOnClone,
            cancelOnMoveOut,
            cancelOnShiftClick,
            clearCursorOnClose,
            closeOnOutsideClick;

    /**
     * An initialized view is one that has already been registered if there is a ViewFrame or has
     * been initialized manually. It is not possible to perform certain operations if the view has
     * already been initialized.
     */
    private boolean initialized;

    AbstractView(int size, String title, @NotNull ViewType type) {
        final int fixedSize = size == 0 ? type.getMaxSize() : type.normalize(size);
        this.size = fixedSize;
        this.title = title;
        this.type = type;

        setItems(new ViewItem[fixedSize]);
    }

    /**
     * Called before the inventory is opened to the player.
     *
     * <p>This handler is often called "pre-rendering" because it is possible to set the title and
     * size of the inventory and also cancel the opening of the View without even doing any handling
     * related to the inventory.
     *
     * <p>It is not possible to manipulate the inventory in this handler, if it happens an exception
     * will be thrown.
     *
     * @param context The player view context.
     */
    protected void onOpen(@NotNull OpenViewContext context) {}

    /**
     * Called when this view is rendered to the player for the first time.
     *
     * <p>This is where you will define items that will be contained non-persistently in the
     * context.
     *
     * <p>Using {@link View#slot(int)} here will cause a leak of items in memory or that the item
     * that was previously defined will be overwritten as the slot item definition method is for use
     * in the constructor only once. Instead, you should use the context item definition function
     * {@link ViewContext#slot(int)}.
     *
     * <p>Handlers call order:
     *
     * <ul>
     *   <li>{@link #onOpen(OpenViewContext)}
     *   <li>this rendering function
     *   <li>{@link #onUpdate(ViewContext)}
     *   <li>{@link #onClose(ViewContext)}
     * </ul>
     *
     * <p>This is a rendering function and can modify the view's container, it's called once.
     *
     * @param context The player view context.
     */
    protected void onRender(@NotNull ViewContext context) {}

    /**
     * Called when the view is updated for a player.
     *
     * <p>This is a rendering function and can modify the view's inventory.
     *
     * @param context The player view context.
     * @see View#update()
     * @see ViewContext#update()
     */
    protected void onUpdate(@NotNull ViewContext context) {}

    /**
     * Called when the player closes the view's inventory.
     *
     * <p>It is possible to cancel this event and have the view's inventory open again for the
     * player.
     *
     * @param context The player view context.
     */
    protected void onClose(@NotNull ViewContext context) {}

    /**
     * Called when a player clicks on the view inventory.
     *
     * <p>This function is called even if the click has been cancelled, you can check this using
     * {@link ViewSlotContext#isCancelled()}.
     *
     * <p>Canceling the context will cancel the click.
     *
     * <p>Handling the inventory in the click handler is not allowed.
     *
     * @param context The player view context.
     * @deprecated Use {@link #onClick(ViewSlotClickContext)} instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.4")
    protected void onClick(@NotNull ViewSlotContext context) {}

    /**
     * Called when an actor clicks on a container while it has a view open.
     *
     * <p>You can know if the click was on entity inventory or view inventory by {@link
     * ViewSlotContext#isOnEntityContainer()}
     *
     * <p>Any function that triggers an {@link #inventoryModificationTriggered() inventory
     * modification} is prohibited from being used in this handler.
     *
     * <p>This context is cancelable and canceling this context will cancel the click, thus
     * canceling all subsequent interceptors causing the pipeline to terminate immediately.
     *
     * @param context The click context.
     */
    protected void onClick(@NotNull ViewSlotClickContext context) {}

    /**
     * Called when the player who clicks outside the view of containers, neither the view's
     * container nor the player's own container.
     *
     * @param context The click context.
     * @deprecated Use {@link #onClick(ViewSlotContext)} with {@link
     *     ViewSlotClickContext#isOutsideClick()} instead.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    protected void onClickOutside(@NotNull ViewContext context) {}

    /**
     * Called when a player uses the hot bar key button.
     *
     * <p>This context is non-cancelable.
     *
     * @param context The current view context.
     * @param hotbarButton The interacted hot bar button.
     * @deprecated Use {@link #onClick(ViewSlotContext)} with {@link
     *     ViewSlotClickContext#isKeyboardClick()} instead.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    protected void onHotbarInteract(@NotNull ViewContext context, int hotbarButton) {}

    /**
     * Called when the player holds an item in the inventory.
     *
     * <p>This handler will only work if the player manages to successfully hold the item, for
     * example it will not be called if the click has been canceled for whatever reasons.
     *
     * <p>This context is non-cancelable.
     *
     * @param context The player view context.
     */
    protected void onItemHold(@NotNull ViewSlotContext context) {}

    /**
     * Called when an item is dropped by the player in an inventory (not necessarily the View's
     * inventory).
     *
     * <p>With this it is possible to detect if the player held and released an item:
     *
     * <ul>
     *   <li>inside the view
     *   <li>outside the view (in the player inventory)
     *   <li>from inside to outside the view (to the player inventory)
     *   <li>from outside to inside the view (from the player inventory)
     * </ul>
     *
     * <p>This handler is the counterpart of {@link #onItemHold(ViewSlotContext)}.
     *
     * @param fromContext The input context of the move.
     * @param toContext The output context of the move.
     */
    protected void onItemRelease(
            @NotNull ViewSlotContext fromContext, @NotNull ViewSlotContext toContext) {}

    /**
     * Called when a player moves a view item out of the view's inventory.
     *
     * <p>Canceling the context will cancel the move. Don't confuse moving with dropping.
     *
     * @param context The player view context.
     */
    protected void onMoveOut(@NotNull ViewSlotMoveContext context) {}

    final void open(@NotNull Viewer viewer, @NotNull Map<String, Object> data) {
        final OpenViewContext open = internalOpen(viewer, data);

        // wait for asynchronous open
        if (open.getJob() != null) {
            open.getJob()
                    .whenComplete(
                            ($, error) -> {
                                postOpen(viewer, open);
                            })
                    .exceptionally(
                            error -> {
                                throwException(open, new RuntimeException(error));
                                return null;
                            });
            return;
        }

        postOpen(viewer, open);
    }

    private void postOpen(@NotNull Viewer viewer, @NotNull OpenViewContext openContext) {
        if (openContext.isCancelled()) return;

        final String containerTitle =
                openContext.getContainerTitle() == null ? title : openContext.getContainerTitle();
        final ViewType containerType =
                openContext.getContainerType() == null ? type : openContext.getContainerType();

        // rows will be normalized to fixed container size on `createContainer`
        final int containerSize =
                openContext.getContainerSize() == 0
                        ? size
                        : containerType.normalize(openContext.getContainerSize());

        final ViewContainer container =
                viewFrame
                        .getFactory()
                        .createContainer(this, containerSize, containerTitle, containerType);

        final BaseViewContext context = viewFrame.getFactory().createContext(this, container, null);
        context.setItems(new ViewItem[containerSize]);
        context.addViewer(viewer);
        openContext.getData().forEach(context::set);
        contexts.add(context);
        render(context);
        context.getViewers().forEach(context.getContainer()::open);
    }

    private OpenViewContext internalOpen(
            @NotNull Viewer viewer, @NotNull Map<String, Object> data) {
        final OpenViewContext context =
                (OpenViewContext)
                        getViewFrame()
                                .getFactory()
                                .createContext(this, null, OpenViewContext.class);

        context.addViewer(viewer);
        data.forEach(context::set);

        getPipeline().execute(OPEN, context);
        runCatching(context, () -> onOpen(context));
        return context;
    }

    @Override
    void render(@NotNull ViewContext context) {
        if (!initialized) throw new IllegalStateException("Cannot render a unitialized view.");

        getPipeline().execute(RENDER, context);
        onRender(context);
        super.render(context);
    }

    @Override
    void update(@NotNull ViewContext context) {
        getPipeline().execute(UPDATE, context);
        onUpdate(context);
        super.update(context);
    }

    /** {@inheritDoc} */
    @Override
    public final void close() {
        // global closings must be always immediate
        closeUninterruptedly();
    }

    /** {@inheritDoc} */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.2")
    public final void closeNow() {
        closeUninterruptedly();
    }

    /** {@inheritDoc} */
    @Override
    public final void closeUninterruptedly() {
        getContexts().forEach(ViewContext::close);
    }

    public final Set<ViewContext> getContexts() {
        return Collections.unmodifiableSet(contexts);
    }

    final ViewContext getContext(@NotNull Predicate<ViewContext> predicate) {
        return contexts.stream().filter(predicate).findFirst().orElse(null);
    }

    public final boolean isCancelOnClick() {
        return cancelOnClick;
    }

    public final boolean isCancelOnClone() {
        return cancelOnClone;
    }

    public final boolean isCancelOnPickup() {
        return cancelOnPickup;
    }

    public final boolean isCancelOnDrop() {
        return cancelOnDrop;
    }

    public final boolean isCancelOnDrag() {
        return cancelOnDrag;
    }

    public final boolean isCancelOnMoveOut() {
        return cancelOnMoveOut;
    }

    public final boolean isCancelOnShiftClick() {
        return cancelOnShiftClick;
    }

    public final boolean isClearCursorOnClose() {
        return clearCursorOnClose;
    }

    public final boolean isCloseOnOutsideClick() {
        return closeOnOutsideClick;
    }

    final void setViewFrame(@NotNull PlatformViewFrame<?, ?, ?> viewFrame) {
        ensureNotInitialized();
        this.viewFrame = viewFrame;
    }

    public final void setCancelOnClick(final boolean cancelOnClick) {
        ensureNotInitialized();
        this.cancelOnClick = cancelOnClick;
    }

    public final void setCancelOnPickup(final boolean cancelOnPickup) {
        ensureNotInitialized();
        this.cancelOnPickup = cancelOnPickup;
    }

    public final void setCancelOnDrop(final boolean cancelOnDrop) {
        ensureNotInitialized();
        this.cancelOnDrop = cancelOnDrop;
    }

    public final void setCancelOnDrag(final boolean cancelOnDrag) {
        ensureNotInitialized();
        this.cancelOnDrag = cancelOnDrag;
    }

    public final void setCancelOnClone(final boolean cancelOnClone) {
        ensureNotInitialized();
        this.cancelOnClone = cancelOnClone;
    }

    public final void setCancelOnMoveOut(final boolean cancelOnMoveOut) {
        ensureNotInitialized();
        this.cancelOnMoveOut = cancelOnMoveOut;
    }

    public final void setCancelOnShiftClick(final boolean cancelOnShiftClick) {
        ensureNotInitialized();
        this.cancelOnShiftClick = cancelOnShiftClick;
    }

    public final void setClearCursorOnClose(final boolean clearCursorOnClose) {
        ensureNotInitialized();
        this.clearCursorOnClose = clearCursorOnClose;
    }

    public final void setCloseOnOutsideClick(final boolean closeOnOutsideClick) {
        ensureNotInitialized();
        this.closeOnOutsideClick = closeOnOutsideClick;
    }

    /** {@inheritDoc} */
    @Override
    protected final ViewItem[] getItems() {
        return super.getItems();
    }

    final Pipeline<ViewContext> getPipeline() {
        return pipeline;
    }

    public final @Nullable PlatformViewFrame<?, ?, ?> getViewFrame() {
        return viewFrame;
    }

    public final @NotNull ViewType getType() {
        return type;
    }

    /** {@inheritDoc} */
    public final int getSize() {
        return size;
    }

    /** {@inheritDoc} */
    @Override
    public final int getRows() {
        return getSize();
    }

    /** {@inheritDoc} */
    public final String getTitle() {
        return title;
    }

    /** {@inheritDoc} */
    @Override
    public final void scheduleUpdate(long delayInTicks, long intervalInTicks) {
        ensureNotInitialized();
        super.scheduleUpdate(delayInTicks, intervalInTicks);
    }

    /** {@inheritDoc} */
    @Override
    public final void inventoryModificationTriggered() {
        super.inventoryModificationTriggered();
    }

    // TODO change 2nd parameter type from Exception to Throwable
    @Override
    final boolean throwException(ViewContext context, @NotNull Exception exception) {
        if (!super.throwException(context, exception)) return false;

        final PlatformViewFrame<?, ?, ?> vf = getViewFrame();
        if (vf == null) return true;

        launchError(vf.getErrorHandler(), context, exception);
        return true;
    }

    /** {@inheritDoc} */
    @ApiStatus.Internal
    @Override
    final ViewItem resolve(int index) {
        return super.resolve(index);
    }

    final void prepareClose(@NotNull CloseViewContext context) {
        getPipeline().execute(CLOSE, context);
    }

    final void remove(@NotNull CloseViewContext context, Viewer viewer) {
        context.getViewers().remove(viewer);
        remove(context);
    }

    final void remove(@NotNull ViewContext context) {
        synchronized (contexts) {
            contexts.remove(context);
        }
    }

    final boolean isInitialized() {
        return initialized;
    }

    final void setInitialized(@SuppressWarnings("SameParameterValue") boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Throws an exception if the view has already been initialized.
     *
     * <p>This method is to be used in cases where the user may mistakenly use functions that must
     * be used in the view constructor, in the handlers, such as: defining the data of a paginated
     * view in the rendering function without using a context.
     *
     * @throws IllegalStateException If this view is initialized.
     */
    protected final void ensureNotInitialized() {
        if (!isInitialized()) return;

        throw new IllegalStateException(
                "Not allowed to change the nature of the view after it has been initialized,"
                        + " it is incorrect to use global functions of the view in render or"
                        + " update functions, whatever method you are trying to call you"
                        + " probably want to call the same function using the context that"
                        + " was provided for you. For example: it is not allowed to use"
                        + " \"setSource(...)\" in the rendering function, you must use "
                        + "\"context.paginated().setSource()\" instead.`");
    }

    @Override
    final int convertSlot(int row, int column) {
        return convertSlot(row, column, getType().getRows(), getType().getColumns());
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Contract(value = " -> this", pure = true)
    public final <T> AbstractPaginatedView<T> paginated() {
        return (AbstractPaginatedView<T>) this;
    }

    /**
     * Schedules a job to run in the next tick.
     *
     * @param job The job that'll be ran.
     */
    protected final void nextTick(@NotNull Runnable job) {
        inventoryModificationTriggered();
        final PlatformViewFrame<?, ?, ?> vf = getViewFrame();
        if (vf == null)
            throw new IllegalStateException("Cannot schedule next tick without a view frame");

        vf.nextTick(job);
    }
}
