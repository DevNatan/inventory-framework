package me.saiintbrisson.minecraft;

import static me.saiintbrisson.minecraft.IFUtils.unwrap;
import static me.saiintbrisson.minecraft.ViewItem.UNSET;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.IFOpenContext;
import me.devnatan.inventoryframework.IFRenderContext;
import me.devnatan.inventoryframework.IFSlotClickContext;
import me.devnatan.inventoryframework.IFSlotContext;
import me.devnatan.inventoryframework.IFSlotMoveContext;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.config.ViewConfig;
import me.devnatan.inventoryframework.exception.ContainerException;
import me.devnatan.inventoryframework.exception.InitializationException;
import me.devnatan.inventoryframework.internal.Job;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.state.MutableState;
import me.devnatan.inventoryframework.state.PaginationState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateHolder;
import me.saiintbrisson.minecraft.logging.Logger;
import me.saiintbrisson.minecraft.pipeline.interceptors.AvailableSlotRenderInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.LayoutPatternApplierInterceptor;
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
@ApiStatus.NonExtendable
public abstract class AbstractView extends AbstractVirtualView {

    // Flags
    public static final byte CANCEL_CLICK = 0, CANCEL_DRAG = 1, CANCEL_PICKUP = 2, CANCEL_DROP = 4, CANCEL_CLONE = 8;

    public static final PipelinePhase OPEN = new PipelinePhase("open");
    public static final PipelinePhase INIT = new PipelinePhase("init");
    public static final PipelinePhase RENDER = new PipelinePhase("render");
    public static final PipelinePhase UPDATE = new PipelinePhase("update");
    public static final PipelinePhase CLICK = new PipelinePhase("click");
    public static final PipelinePhase CLOSE = new PipelinePhase("close");
    public static final ViewType DEFAULT_TYPE = ViewType.CHEST;

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
    private final Set<IFContext> contexts = Collections.newSetFromMap(Collections.synchronizedMap(new HashMap<>()));
    private final Pipeline<VirtualView> pipeline = new Pipeline<>(INIT, OPEN, RENDER, UPDATE, CLICK, CLOSE);
    private Logger logger;

    private Job updateJob;
    long[] updateSchedule;

    /**
     * An initialized view is one that has already been registered if there is a ViewFrame or has
     * been initialized manually. It is not possible to perform certain operations if the view has
     * already been initialized.
     */
    private boolean initialized;

    private final ViewInitialProperties initialProperties = new ViewInitialProperties();

    protected AbstractView() {
        this(0, null, ViewType.CHEST);
    }

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
     * {@inheritDoc}
     *
     * @throws InitializationException If this view is initialized.
     */
    @Override
    public final @NotNull ViewItem slot(int slot, Object item) {
        ensureNotInitialized();
        return super.slot(slot, item);
    }

    public final @NotNull Logger getLogger() {
        return logger == null ? PlatformUtils.getFactory().getLogger() : logger;
    }

    final void open(@NotNull Viewer viewer, @NotNull Map<String, Object> data, @Nullable IFContext initiator) {
        if (!isInitialized()) throw new IllegalStateException("Cannot open a uninitialized view.");

        final IFOpenContext context = (IFOpenContext) PlatformUtils.getFactory()
			.createContext(this, null, IFOpenContext.class, viewer);

        context.addViewer(viewer);
        context.setPrevious((BaseViewContext) initiator);
        data.forEach(context::set);
        onOpen(context);
        getPipeline().execute(OPEN, context);
    }

    @Override
    public void render(@NotNull IFContext context) {
        if (!isInitialized()) throw new IllegalStateException("Cannot render a uninitialized view.");

        runCatching(context, () -> {
            onRender(context);
            getPipeline().execute(RENDER, context);
        });
    }

    @Override
    public void update(@NotNull IFContext context) {
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
        getContexts().forEach(IFContext::close);
    }

    public final Set<IFContext> getContexts() {
        return Collections.unmodifiableSet(contexts);
    }

    final IFContext getContext(@NotNull Predicate<IFContext> predicate) {
        return contexts.stream().filter(predicate).findFirst().orElse(null);
    }

    public final void registerContext(@NotNull IFContext context) {
        synchronized (contexts) {
            contexts.add(context);
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final void inventoryModificationTriggered() {
        super.inventoryModificationTriggered();
    }

    // TODO change 2nd parameter type from Exception to Throwable
    @Override
    final boolean throwException(IFContext context, @NotNull Exception exception) throws Exception {
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
        for (final IFContext context : new ArrayList<>(contexts)) context.render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void update() {
        for (final IFContext context : new ArrayList<>(contexts)) context.update();
    }

    final void prepareClose(@NotNull IFContext context) {
        getPipeline().execute(CLOSE, context);
    }

    final void remove(@NotNull IFContext context, Viewer viewer) {
        synchronized (context.getViewers()) {
            context.removeViewer(viewer);

            // fast path -- only remove context if all viewers are gone
            if (!context.getViewers().isEmpty()) return;
        }

        remove(context);
    }

    final void remove(@NotNull IFContext context) {
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

    @ApiStatus.Internal
    public final boolean render(IFContext parent, IFSlotContext context, ViewItem item, int slot) {
        final IFSlotContext renderContext = context == null
                ? PlatformUtils.getFactory().createSlotContext(slot, item, parent, parent.getContainer(), UNSET, null)
                : context;

        runCatching(context, () -> onSlotRender(renderContext));

        if (item != null) {
            ViewItemHandler renderHandler = item.getRenderHandler();
            if (renderHandler != null) runCatching(context, () -> renderHandler.handle(renderContext));
        }

        if (renderContext.hasChanged()) {
            renderContext.getContainer().renderItem(slot, unwrap(renderContext.getItemWrapper()));
            renderContext.setChanged(false);
            return true;
        }

        return false;
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
    public final void render(@NotNull IFContext context, @NotNull ViewItem item, int slot) {
        inventoryModificationTriggered();

        // the item's slot has not yet been determined because of using the auto-set slot function
        // and must be applied during rendering.
        if (item.getSlot() == ViewItem.AVAILABLE) item.setSlot(slot);

        if (render(context, null, item, slot) /* modified */) return;

        final Object fallbackItem = item.getItem();
        if (fallbackItem == null) {
            throw new IllegalArgumentException(String.format(
                    "No item were provided and the rendering function was not defined at slot %d."
                            + "You must use a rendering function #slot(...).onRender(...)"
                            + " or a fallback item #slot(fallbackItem)",
                    slot));
        }

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
    public final void update(@NotNull IFContext context, ViewItem item, int slot) {
        if (item.isRemoved()) {
            try {
                context.getContainer().renderItem(slot, null);
            } catch (Exception e) {
                throw new ContainerException(null, e);
            }
            return;
        }

        if (item.getUpdateHandler() != null) {
            final IFSlotContext updateContext = PlatformUtils.getFactory()
                    .createSlotContext(slot, item, context, context.getContainer(), UNSET, null);

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

    /**
     * The update job for this view.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @return The update job for this view.
     */
    @ApiStatus.Internal
    public final Job getUpdateJob() {
        return updateJob;
    }

    /**
     * Sets the update job for this view.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param updateJob The new update job.
     */
    @ApiStatus.Internal
    public final void setUpdateJob(Job updateJob) {
        this.updateJob = updateJob;
    }

    /**
     * Checks if this view is set to update automatically.
     *
     * @return <code>true</code> if it will update automatically or <code>false</code> otherwise.
     */
    public final boolean isScheduledToUpdate() {
        return updateJob != null || updateSchedule != null;
    }

    /**
     * Schedules this view to update every fixed interval by calling {@link #update()}.
     * <p>
     * The job will only remain active as long as there are viewers present in this view.
     *
     * @param delayInTicks    The initial delay before job start.
     * @param intervalInTicks The interval between updates.
     */
    public final void scheduleUpdate(long delayInTicks, long intervalInTicks) {
        ensureNotInitialized();

        if (intervalInTicks <= -1)
            throw new IllegalArgumentException("Schedule update interval in ticks must be greater than -1.");

        final Job old = getUpdateJob();
        if (old != null && old.isStarted()) old.cancel();

        updateSchedule = new long[] {delayInTicks, intervalInTicks};
    }

    /**
     * Schedules this view to update every fixed interval by calling {@link #update()}.
     * <p>
     * The job will only remain active as long as there are viewers present in this view.
     *
     * @param intervalInTicks The interval between updates.
     */
    public final void scheduleUpdate(long intervalInTicks) {
        scheduleUpdate(-1, intervalInTicks);
    }

    /**
     * Schedules this view to update every fixed interval by calling {@link #update()}.
     * <p>
     * The job will only remain active as long as there are viewers present in this view.
     *
     * @param interval The interval between updates.
     */
    public final void scheduleUpdate(@NotNull Duration interval) {
        scheduleUpdate(-1, interval.getSeconds() * 20 /* 1 second = 20 ticks */);
    }

    // TODO apply it's own pipeline order for each view kind
    @ApiStatus.OverrideOnly
    void beforeInit() {
        final Pipeline<VirtualView> pipeline = getPipeline();
        pipeline.intercept(OPEN, new OpenInterceptor());
        pipeline.intercept(INIT, new LayoutResolutionInterceptor());
        pipeline.intercept(INIT, new LayoutPatternApplierInterceptor());
        pipeline.intercept(RENDER, new LayoutResolutionInterceptor() /* context scope */);
        pipeline.intercept(RENDER, new LayoutPatternApplierInterceptor() /* context scope */);
        pipeline.intercept(RENDER, new AvailableSlotRenderInterceptor());
        pipeline.intercept(RENDER, new RenderInterceptor());
        pipeline.intercept(RENDER, new ScheduledUpdateInterceptor.Render());
        pipeline.intercept(UPDATE, new UpdateInterceptor());
        pipeline.intercept(CLOSE, new ScheduledUpdateInterceptor.Close());
    }

    void initUpdateScheduler() {
        final long[] timing = updateSchedule;
        if (timing == null) return;

        final PlatformViewFrame<?, ?, ?> initiator = IFUtils.findViewFrame(this);
        if (initiator == null) throw new IllegalStateException("No initiator to schedule update.");

        final Job job = Objects.requireNonNull(
                initiator.schedule(this::update, timing[1], timing[0]), "Job scheduled by initiator cannot be null.");

        setUpdateJob(job);
    }

    final void init(boolean forTests) {
        ensureNotInitialized();
        if (!forTests) {
            beforeInit();
            onInit();
            getPipeline().execute(INIT, this);
            initUpdateScheduler();
        }
        setInitialized(true);
    }

    @Override
    public void emit(@NotNull String event, Object value) {
        super.emit(event, value);
        getContexts().forEach(context -> context.emit(event, value));
    }

    @Override
    public void emit(@NotNull Object event) {
        super.emit(event);
        getContexts().forEach(context -> context.emit(event));
    }

}
