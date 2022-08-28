package me.saiintbrisson.minecraft;

import static me.saiintbrisson.minecraft.IFUtils.unwrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.saiintbrisson.minecraft.exception.ContainerException;
import me.saiintbrisson.minecraft.exception.InitializationException;
import me.saiintbrisson.minecraft.pipeline.Pipeline;
import me.saiintbrisson.minecraft.pipeline.PipelinePhase;
import me.saiintbrisson.minecraft.pipeline.interceptors.AvailableSlotRenderInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.LayoutPatternRenderInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.LayoutResolutionInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.OpenInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.RenderInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.ScheduledUpdateInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.UpdateInterceptor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractView extends AbstractVirtualView {

    public static final PipelinePhase OPEN = new PipelinePhase("open");
    public static final PipelinePhase INIT = new PipelinePhase("init");
    public static final PipelinePhase RENDER = new PipelinePhase("render");
    public static final PipelinePhase UPDATE = new PipelinePhase("update");
    public static final PipelinePhase CLICK = new PipelinePhase("click");
    public static final PipelinePhase CLOSE = new PipelinePhase("close");
    static final ViewType DEFAULT_TYPE = ViewType.CHEST;

    @ToString.Include
    private final int size;

    @ToString.Include
    private final String title;

    @ToString.Include
    private final ViewType type;

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

    private final int columns;
    private final int rows;
    PlatformViewFrame<?, ?, ?> viewFrame;
    private final Set<ViewContext> contexts = Collections.newSetFromMap(Collections.synchronizedMap(new HashMap<>()));
    private final Pipeline<VirtualView> pipeline = new Pipeline<>(INIT, OPEN, RENDER, UPDATE, CLICK, CLOSE);

    /**
     * An initialized view is one that has already been registered if there is a ViewFrame or has been
     * initialized manually. It is not possible to perform certain operations if the view has already
     * been initialized.
     */
    private boolean initialized;

    protected AbstractView(int size, String title, @NotNull ViewType type) {
        final int fixedSize = size == 0 ? type.getMaxSize() : type.normalize(size);
        this.size = fixedSize;
        this.rows = fixedSize / type.getColumns();
        this.columns = type.getColumns();
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
     * Called __once__ when this view is rendered to the player for the first time.
     *
     * <p>This is where you will define items that will be contained non-persistently in the context.
     *
     * <p>Using {@link View#slot(int)} here will cause a leak of items in memory or that the item that
     * was previously defined will be overwritten as the slot item definition method is for use in the
     * constructor only once. Instead, you should use the context item definition function {@link
     * ViewContext#slot(int)}.
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
     * <p>It is possible to cancel this event and have the view's inventory open again for the player.
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
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
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
     * <p>This context is cancelable and canceling this context will cancel the click, thus canceling
     * all subsequent interceptors causing the pipeline to terminate immediately.
     *
     * @param context The click context.
     */
    protected void onClick(@NotNull ViewSlotClickContext context) {}

    /**
     * Called when the player who clicks outside the view of containers, neither the view's container
     * nor the player's own container.
     *
     * @param context The click context.
     * @deprecated Use {@link #onClick(ViewSlotContext)} with {@link
     * ViewSlotClickContext#isOutsideClick()} instead.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    protected void onClickOutside(@NotNull ViewContext context) {}

    /**
     * Called when a player uses the hot bar key button.
     *
     * <p>This context is non-cancelable.
     *
     * @param context      The current view context.
     * @param hotbarButton The interacted hot bar button.
     * @deprecated Use {@link #onClick(ViewSlotContext)} with {@link
     * ViewSlotClickContext#isKeyboardClick()} instead.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    protected void onHotbarInteract(@NotNull ViewContext context, int hotbarButton) {}

    /**
     * Called when the player holds an item in the inventory.
     *
     * <p>This handler will only work if the player manages to successfully hold the item, for example
     * it will not be called if the click has been canceled for whatever reasons.
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
     * @param toContext   The output context of the move.
     */
    protected void onItemRelease(@NotNull ViewSlotContext fromContext, @NotNull ViewSlotContext toContext) {}

    /**
     * Called when a player moves a view item out of the view's inventory.
     *
     * <p>Canceling the context will cancel the move. Don't confuse moving with dropping.
     *
     * @param context The player view context.
     */
    protected void onMoveOut(@NotNull ViewSlotMoveContext context) {}

    /**
     * Called when a context is resumed.
     * <p>
     * Currently, there is only one way to resume a context, which is through
     * {@link ViewContext#back()} the "context" parameter being the context that was resumed and
     * the "subject" the context in which the function was executed.
     *
     * @param context The resumed context.
     * @param subject By what context the context was resumed.
     */
    protected void onResume(@NotNull ViewContext context, @NotNull ViewContext subject) {}

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public @NotNull ViewItem slot(int slot, Object item) {
        ensureNotInitialized();
        return super.slot(slot, item);
    }

    final void open(@NotNull Viewer viewer, @NotNull Map<String, Object> data, @Nullable ViewContext initiator) {
        if (!isInitialized()) throw new IllegalStateException("Cannot open a uninitialized view.");

        final OpenViewContext context =
                (OpenViewContext) PlatformUtils.getFactory().createContext(this, null, OpenViewContext.class);

        context.addViewer(viewer);
        context.setPrevious((BaseViewContext) initiator);
        data.forEach(context::set);
        onOpen(context);
        getPipeline().execute(OPEN, context);
    }

    @Override
    public void render(@NotNull ViewContext context) {
        if (!isInitialized()) throw new IllegalStateException("Cannot render a uninitialized view.");

        runCatching(context, () -> {
            onRender(context);
            getPipeline().execute(RENDER, context);
        });
    }

    @Override
    public void update(@NotNull ViewContext context) {
        runCatching(context, () -> {
            onUpdate(context);
            getPipeline().execute(UPDATE, context);
        });
    }

    /**
     * Resumes a context.
     * <p>
     * Basically this will open the context expecting that this context has already been initialized
     * and populated previously.
     *
     * @param target  The context that will be resumed.
     * @param subject The context that asked for that context to be resumed.
     */
    void resume(@NotNull BaseViewContext target, @NotNull BaseViewContext subject) {
        target.setPrevious(subject);

        // we need to copy since this will be and close -> open -> close operation
        // and open the target for each viewer from the subject context
        final List<Viewer> viewers = new ArrayList<>(subject.internalGetViewers());
        System.out.println("coming viewers (" + viewers.size() + ") " + viewers);

        viewers.forEach(viewer -> {
            target.addViewer(viewer);
            target.getContainer().open(viewer);
        });

        final AbstractView root = target.getRoot();
        root.registerContext(target);
        root.onResume(target, subject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void close() {
        // global closings must be always immediate
        closeUninterruptedly();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    public final void closeNow() {
        closeUninterruptedly();
    }

    /**
     * {@inheritDoc}
     */
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

    public final void registerContext(@NotNull ViewContext context) {
        contexts.add(context);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final ViewItem[] getItems() {
        return super.getItems();
    }

    public final Pipeline<VirtualView> getPipeline() {
        return pipeline;
    }

    public final @Nullable PlatformViewFrame<?, ?, ?> getViewFrame() {
        return viewFrame;
    }

    public final @NotNull ViewType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public final int getSize() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getRows() {
        return rows;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public int getColumns() {
        return columns;
    }

    /**
     * {@inheritDoc}
     */
    public final String getTitle() {
        return title;
    }

    @Override
    public final ViewUpdateJob getUpdateJob() {
        return super.getUpdateJob();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void scheduleUpdate(long delayInTicks, long intervalInTicks) {
        ensureNotInitialized();
        super.scheduleUpdate(delayInTicks, intervalInTicks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void inventoryModificationTriggered() {
        super.inventoryModificationTriggered();
    }

    // TODO change 2nd parameter type from Exception to Throwable
    @Override
    final boolean throwException(ViewContext context, @NotNull Exception exception) throws Exception {
        if (!super.throwException(context, exception)) return false;

        final PlatformViewFrame<?, ?, ?> vf = getViewFrame();
        if (vf == null) return true;

        launchError(vf.getErrorHandler(), context, exception);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @ApiStatus.Internal
    @Override
    public final ViewItem resolve(int index, boolean resolveOnRoot) {
        return super.resolve(index, resolveOnRoot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void render() {
        for (final ViewContext context : new ArrayList<>(contexts)) context.render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void update() {
        for (final ViewContext context : new ArrayList<>(contexts)) context.update();
    }

    final void prepareClose(@NotNull ViewContext context) {
        getPipeline().execute(CLOSE, context);
    }

    final void remove(@NotNull ViewContext context, Viewer viewer) {
        synchronized (context.getViewers()) {
            context.removeViewer(viewer);

            // fast path -- only remove context if all viewers are gone
            if (!context.getViewers().isEmpty()) return;
        }

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
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     **/
    @Override
    public final void setLayout(@Nullable String... layout) {
        ensureNotInitialized();
        super.setLayout(layout);
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     **/
    @Override
    public final void setLayout(char identifier, @Nullable Consumer<ViewItem> layout) {
        ensureNotInitialized();
        super.setLayout(identifier, layout);
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final void setLayout(char character, @Nullable Supplier<ViewItem> factory) {
        ensureNotInitialized();
        super.setLayout(character, factory);
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final void setErrorHandler(ViewErrorHandler errorHandler) {
        ensureNotInitialized();
        super.setErrorHandler(errorHandler);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Contract(value = " -> this", pure = true)
    public final <T> AbstractPaginatedView<T> paginated() {
        return (AbstractPaginatedView<T>) this;
    }

    /**
     * Schedules a job to run in the next tick.
     *
     * @param job The job that'll be run.
     */
    public final void nextTick(@NotNull Runnable job) {
        inventoryModificationTriggered();
        final PlatformViewFrame<?, ?, ?> vf = getViewFrame();
        if (vf == null) throw new IllegalStateException("Cannot schedule next tick without a view frame");

        vf.nextTick(job);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final int convertSlot(int row, int column) {
        return IFUtils.convertSlot(row, column, getType().getRows(), getType().getColumns());
    }

    /**
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final @NotNull ViewItem availableSlot(Object fallbackItem) {
        ensureNotInitialized();
        return super.availableSlot(fallbackItem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    int getNextAvailableSlot() {
        if (getLayout() == null) {
            for (int i = 0; i < size; i++) {
                // fast path -- skip resolution if slot isn't interactable
                if (!type.canPlayerInteractOn(i)) continue;

                // slow path -- resolve slot one by one
                if (getItem(i) != null) continue;

                return i;
            }
        }

        return ViewItem.AVAILABLE;
    }

    /**
     * Renders a item.
     *
     * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param context The context.
     * @param item    The item.
     * @param slot    The target slot.
     */
    @ApiStatus.Internal
    public final void render(@NotNull ViewContext context, @NotNull ViewItem item, int slot) {
        inventoryModificationTriggered();

        // the item's slot has not yet been determined because of using the auto-set slot function
        // and must be applied during rendering.
        if (item.getSlot() == ViewItem.AVAILABLE) item.setSlot(slot);

        final Object fallbackItem = item.getItem();

        if (item.getRenderHandler() != null) {
            final ViewSlotContext renderContext = PlatformUtils.getFactory().createSlotContext(item, context, 0, null);

            runCatching(context, () -> item.getRenderHandler().handle(renderContext));
            if (renderContext.hasChanged()) {
                context.getContainer().renderItem(slot, unwrap(renderContext.getItemWrapper()));
                renderContext.setChanged(false);
                return;
            }
        }

        if (fallbackItem == null)
            throw new IllegalArgumentException(String.format(
                    "No item were provided and the rendering function was not defined at slot %d."
                            + "You must use a rendering function #slot(...).onRender(...)"
                            + " or a fallback item #slot(fallbackItem)",
                    slot));

        try {
            context.getContainer().renderItem(slot, unwrap(fallbackItem));
        } catch (Exception e) {
            throw new ContainerException(null, e);
        }
    }

    /**
     * Updates a item.
     *
     * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param context The context.
     * @param item    The item.
     * @param slot    The target slot.
     */
    @ApiStatus.Internal
    public final void update(@NotNull ViewContext context, ViewItem item, int slot) {
        if (item.isRemoved()) {
            try {
                context.getContainer().renderItem(slot, null);
            } catch (Exception e) {
                throw new ContainerException(null, e);
            }
            return;
        }

        if (item.getUpdateHandler() != null) {
            final ViewSlotContext updateContext = PlatformUtils.getFactory().createSlotContext(item, context, 0, null);

            runCatching(context, () -> item.getUpdateHandler().handle(updateContext));
            if (updateContext.hasChanged()) {
                context.getContainer().renderItem(slot, unwrap(updateContext.getItemWrapper()));
                updateContext.setChanged(false);
                return;
            }
        }

        // update handler can be used as an empty function, so we fall back to the render handler to
        // update the fallback item properly
        render(context, item, slot);
    }

    /**
     * Throws an exception if this view has already been initialized.
     * <p>
     * This method is to be used in cases where the user may mistakenly use functions that must be
     * used in the view constructor, in the handlers, such as: defining the data of a paginated view
     * in the rendering function without using a context.
     *
     * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @throws InitializationException If this view is initialized.
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Errors#initializationexception">InitializationException on Wiki</a>
     */
    @ApiStatus.Internal
    public final void ensureNotInitialized() {
        if (!isInitialized()) return;
        throw new InitializationException();
    }

    @ApiStatus.OverrideOnly
    void beforeInit() {
        final Pipeline<VirtualView> pipeline = getPipeline();
        pipeline.intercept(OPEN, new OpenInterceptor());
        pipeline.intercept(INIT, new LayoutResolutionInterceptor());
        pipeline.intercept(INIT, new LayoutPatternRenderInterceptor());
        pipeline.intercept(RENDER, new LayoutResolutionInterceptor() /* context scope */);
        pipeline.intercept(RENDER, new LayoutPatternRenderInterceptor() /* context scope */);
        pipeline.intercept(RENDER, new AvailableSlotRenderInterceptor());
        pipeline.intercept(RENDER, new RenderInterceptor());
        pipeline.intercept(RENDER, new ScheduledUpdateInterceptor.Render());
        pipeline.intercept(UPDATE, new UpdateInterceptor());
        pipeline.intercept(CLOSE, new ScheduledUpdateInterceptor.Close());
    }

    final void init() {
        ensureNotInitialized();
        beforeInit();
        getPipeline().execute(INIT, this);
        setInitialized(true);
    }
}
