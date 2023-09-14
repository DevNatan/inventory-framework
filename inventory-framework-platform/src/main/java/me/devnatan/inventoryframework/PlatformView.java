package me.devnatan.inventoryframework;

import static java.lang.String.format;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.component.*;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.PlatformRenderContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.PlatformUtils;
import me.devnatan.inventoryframework.pipeline.AvailableSlotInterceptor;
import me.devnatan.inventoryframework.pipeline.FirstRenderInterceptor;
import me.devnatan.inventoryframework.pipeline.LayoutRenderInterceptor;
import me.devnatan.inventoryframework.pipeline.LayoutResolutionInterceptor;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PlatformCloseInterceptor;
import me.devnatan.inventoryframework.pipeline.PlatformInitInterceptor;
import me.devnatan.inventoryframework.pipeline.PlatformOpenInterceptor;
import me.devnatan.inventoryframework.pipeline.PlatformRenderInterceptor;
import me.devnatan.inventoryframework.pipeline.PlatformUpdateHandlerInterceptor;
import me.devnatan.inventoryframework.pipeline.ScheduledUpdateAfterCloseInterceptor;
import me.devnatan.inventoryframework.pipeline.ScheduledUpdateAfterRenderInterceptor;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import me.devnatan.inventoryframework.pipeline.UpdateInterceptor;
import me.devnatan.inventoryframework.pipeline.ViewerLastInteractionTrackerInterceptor;
import me.devnatan.inventoryframework.state.BaseMutableState;
import me.devnatan.inventoryframework.state.BaseState;
import me.devnatan.inventoryframework.state.ComputedValue;
import me.devnatan.inventoryframework.state.ImmutableValue;
import me.devnatan.inventoryframework.state.InitialDataStateValue;
import me.devnatan.inventoryframework.state.LazyValue;
import me.devnatan.inventoryframework.state.MutableGenericStateImpl;
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.MutableIntStateImpl;
import me.devnatan.inventoryframework.state.MutableState;
import me.devnatan.inventoryframework.state.MutableValue;
import me.devnatan.inventoryframework.state.PaginationState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateValueFactory;
import me.devnatan.inventoryframework.state.StateValueHost;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class PlatformView<
                TFramework extends IFViewFrame<?, ?>,
                TItem extends ItemComponentBuilder<TItem, TContext> & ComponentFactory,
                TContext extends IFContext,
                TOpenContext extends IFOpenContext,
                TCloseContext extends IFCloseContext,
                TRenderContext extends IFRenderContext,
                TSlotContext extends IFSlotContext,
                TSlotClickContext extends IFSlotClickContext>
        extends DefaultRootView implements Iterable<TContext> {

    private TFramework framework;
    private boolean initialized;

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    public final TFramework getFramework() {
        return framework;
    }

    /**
     * The initialization state of this view.
     *
     * @return If this view was initialized.
     */
    final boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the initialization state of this view.
     *
     * @param initialized The new initialization state.
     */
    final void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Throws an exception if this view is already initialized.
     *
     * @throws IllegalStateException if this view is already initialized.
     */
    private void requireNotInitialized() {
        if (!isInitialized()) return;
        throw new IllegalStateException(
                "View is already initialized, please move this method call to class constructor or #onInit.");
    }

    /**
     * Closes all contexts that are currently active in this view.
     */
    public final void closeForEveryone() {
        getContexts().forEach(IFContext::closeForEveryone);
    }

    /**
     * Opens this view to one or more viewers.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param viewers     The viewers that'll see this view.
     * @param initialData The initial data.
     */
    @ApiStatus.Internal
    public final void open(@NotNull List<Viewer> viewers, Object initialData) {
        if (!isInitialized()) throw new IllegalStateException("Cannot open a uninitialized view");

        final Viewer subject = viewers.size() == 1 ? viewers.get(0) : null;
        final IFOpenContext context = getElementFactory().createOpenContext(this, subject, viewers, initialData);

        getPipeline().execute(StandardPipelinePhases.OPEN, context);
    }

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @SuppressWarnings("rawtypes")
    @ApiStatus.Internal
    public final void navigateTo(
            @NotNull Class<? extends PlatformView> target, @NotNull IFRenderContext origin, Object initialData) {
        final List<Viewer> viewers = origin.getViewers();
        viewers.forEach(viewer -> setupNavigateTo(viewer, origin));
        getFramework().getRegisteredViewByType(target).open(viewers, initialData);
    }

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @SuppressWarnings("rawtypes")
    @ApiStatus.Internal
    public final void navigateTo(
            @NotNull Class<? extends PlatformView> target,
            @NotNull IFRenderContext origin,
            @NotNull Viewer viewer,
            Object initialData) {
        setupNavigateTo(viewer, origin);
        getFramework().getRegisteredViewByType(target).open(Collections.singletonList(viewer), initialData);
    }

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    public final void back(@NotNull Viewer viewer) {
        back(viewer, null);
    }

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiStatus.Internal
    public final void back(@NotNull Viewer viewer, Object initialData) {
        final IFRenderContext active = viewer.getActiveContext();
        final IFRenderContext target = viewer.getPreviousContext();
        viewer.unsetPreviousContext();
        viewer.setTransitioning(true);
        viewer.setActiveContext(target);
        target.addViewer(viewer);

        for (final Map.Entry<Long, StateValue> entry : target.getStateValues().entrySet()) {
            final StateValue value = entry.getValue();
            if (!(value instanceof InitialDataStateValue)) continue;

            ((InitialDataStateValue) value).reset();
        }

        target.setInitialData(initialData);

        if (target.getViewers().size() == 1) target.getContainer().open(viewer);
        else {
            final PlatformView root = (PlatformView) target.getRoot();
            if (!root.hasContext(target.getId())) root.addContext(target);

            root.renderContext(target);
        }

        ((PlatformView) target.getRoot()).onResume(active, target);
        viewer.setTransitioning(false);
    }

    private void setupNavigateTo(@NotNull Viewer viewer, @NotNull IFRenderContext origin) {
        viewer.setTransitioning(true);
        viewer.setPreviousContext(origin);
    }

    /**
     * Creates a new ViewConfigBuilder instance with the default platform configuration.
     * Configuration is inherited from the {@link #getFramework() framework} if available.
     *
     * @return A new ViewConfigBuilder instance.
     */
    public final @NotNull ViewConfigBuilder createConfig() {
        final ViewConfigBuilder configBuilder = new ViewConfigBuilder().type(ViewType.CHEST);
        if (getFramework().getDefaultConfig() != null)
            getFramework().getDefaultConfig().accept(configBuilder);
        return configBuilder;
    }

    /**
     * Returns the context that is linked to the specified viewer in this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param viewer The viewer.
     * @return The context of the viewer in this context.
     * @throws IllegalArgumentException If there's no context linked to the given viewer.
     */
    @ApiStatus.Internal
    public final @NotNull IFContext getContext(@NotNull Viewer viewer) {
        for (final IFContext context : getInternalContexts()) {
            if (context.getIndexedViewers().containsKey(viewer.getId())) return context;
        }

        throw new IllegalArgumentException(format("Unable to get context for %s", viewer));
    }

    /**
     * Returns the context that is linked to the specified viewer in this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param viewerId The id of the viewer.
     * @return The context of the viewer in this context.
     * @throws IllegalArgumentException If there's no context linked to the given viewer.
     */
    public final @NotNull IFContext getContext(@NotNull String viewerId) {
        for (final IFContext context : getInternalContexts()) {
            if (context.getIndexedViewers().containsKey(viewerId)) return context;
        }

        throw new IllegalArgumentException(format("Unable to get context for %s", viewerId));
    }

    /**
     * Adds a context to this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param context The context to add.
     */
    @ApiStatus.Internal
    public void addContext(@NotNull TContext context) {
        synchronized (getInternalContexts()) {
            getInternalContexts().add(context);
        }
    }

    /**
     * Removes a given context from this view if that context is linked to this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param context The context to remove.
     */
    @ApiStatus.Internal
    public void removeContext(@NotNull IFContext context) {
        synchronized (getInternalContexts()) {
            getInternalContexts().removeIf(other -> other.getId() == context.getId());
        }
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public boolean hasContext(@NotNull UUID id) {
        for (final IFContext context : getInternalContexts()) {
            if (context.getId().equals(id)) return true;
        }
        return false;
    }

    /**
     * Renders a given context in this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param context The context to render.
     */
    @ApiStatus.Internal
    public void renderContext(@NotNull TRenderContext context) {
        getPipeline().execute(context);
        ((PlatformRenderContext<?, ?>) context).setRendered();

        @SuppressWarnings("rawtypes")
        final PlatformView view = (PlatformView) context.getRoot();
        context.getViewers().forEach(viewer -> {
            if (viewer.isTransitioning()) viewer.setActiveContext(context);
            view.getFramework().addViewer(viewer);
            if (!context.getContainer().isProxied()) context.getContainer().open(viewer);
            viewer.setTransitioning(false);
        });
    }

    @SuppressWarnings("rawtypes")
    @ApiStatus.Internal
    public void removeAndTryInvalidateContext(@NotNull Viewer viewer, @NotNull TContext context) {
        context.removeViewer(viewer);

        if (!viewer.isTransitioning())
            ((PlatformView) context.getRoot()).getFramework().removeViewer(viewer);

        final IFRenderContext target =
                viewer.isTransitioning() ? viewer.getPreviousContext() : viewer.getActiveContext();

        if (target == null) return;

        if (target.getViewers().isEmpty()) {
            // TODO Disable context (setActive(false)) if viewer is transitioning
            removeContext(target);
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public final Iterator<TContext> iterator() {
        return (Iterator<TContext>) getContexts().iterator();
    }

	/**
	 * Creates a new unassigned reference instance.
	 * <p>
	 * <b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @return A new unassigned {@link Ref} instance.
	 * @param <E> Type of the element hold by this reference.
	 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/refs-api">Refs API on Wiki</a>
	 */
	@ApiStatus.Experimental
	protected final <E> Ref<E> ref() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

    /**
     * Creates an immutable state with an initial value.
     *
     * <pre>{@code
     * State<String> textState = state("test");
     *
     * intState.get(...); // "test"
     * }</pre>
     *
     * @param initialValue The initial value of the state.
     * @param <T>          The state value type.
     * @return A state with an initial value.
     */
    protected final <T> State<T> state(T initialValue) {
        requireNotInitialized();
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new ImmutableValue(state, initialValue);
        final State<T> state = new BaseState<>(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates a {@link MutableState mutable state} with an initial value.
     *
     * <pre>{@code
     * MutableState<String> textState = mutableState("");
     *
     * textState.get(...); // ""
     * textState.set("abc", ...);
     * textState.get(...); // "abc"
     * }</pre>
     *
     * @param initialValue The initial value of the state.
     * @param <T>          The state value type.
     * @return A mutable state with an initial value.
     */
    protected final <T> MutableState<T> mutableState(T initialValue) {
        requireNotInitialized();
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new MutableValue(state, initialValue);
        final MutableState<T> state = new MutableGenericStateImpl<>(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates a {@link MutableState mutable state} with an initial value.
     *
     * <pre>{@code
     * MutableIntState intState = mutableIntState(0);
     *
     * intState.get(...); // 0
     * intState.set(4, ...);
     * intState.get(...); // 4
     * }</pre>
     *
     * @param initialValue The initial value of the state.
     * @return A mutable state with an initial value.
     */
    protected final MutableIntState mutableState(int initialValue) {
        requireNotInitialized();
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new MutableValue(state, initialValue);
        final MutableIntState state = new MutableIntStateImpl(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates an immutable computed state.
     * <p>
     * A computed state is a state that every time an attempt is made to obtain the value of that
     * state, the obtained value is computed again by the {@code computation} function.
     * <pre>{@code
     * State<Integer> intState = computedState($ -> ThreadLocalRandom.current().nextInt());
     *
     * intState.get(...); // some random number
     * intState.get(...); // another random number
     * }</pre>
     *
     * @param computation The function to compute the value.
     * @param <T>         The state value type.
     * @return An immutable computed state.
     */
    protected final <T> State<T> computedState(@NotNull Function<TContext, T> computation) {
        requireNotInitialized();
        final long id = State.next();
        @SuppressWarnings("unchecked")
        final StateValueFactory factory =
                (host, state) -> new ComputedValue(state, () -> computation.apply((TContext) host));
        final State<T> state = new BaseState<>(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates an immutable computed state.
     * <p>
     * A computed state is a state that every time an attempt is made to obtain the value of that
     * state, the obtained value is computed again by the {@code computation} function.
     * <pre>{@code
     * State<Integer> randomIntState = computedState(ThreadLocalRandom.current()::nextInt);
     *
     * randomIntState.get(...); // some random number
     * randomIntState.get(...); // another random number
     * }</pre>
     *
     * @param computation The function to compute the value.
     * @param <T>         The state holder type.
     * @return An immutable computed state.
     */
    protected final <T> State<T> computedState(@NotNull Supplier<T> computation) {
        requireNotInitialized();
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new ComputedValue(state, computation);
        final State<T> state = new BaseState<>(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates an immutable lazy state.
     * <p>
     * {@code factory} defines what the value will be, a holder try to get the value, and the value
     * obtained from there will be the value that will be obtained in subsequent calls to get the
     * value of the state.
     * <pre>{@code
     * State<Integer> intState = lazyState($ -> ThreadLocalRandom.current().nextInt());
     *
     * intState.get(...); // 54 - from initial computation of random integer ^^
     * intState.get(...); // 54 - previously defined by the initial computation
     * }</pre>
     *
     * @param computation The value factory.
     * @param <T>         The state value type.
     * @return A lazy state.
     */
    protected final <T> State<T> lazyState(@NotNull Function<TContext, T> computation) {
        requireNotInitialized();
        final long id = State.next();
        @SuppressWarnings("unchecked")
        final StateValueFactory factory =
                (host, state) -> new LazyValue(state, () -> computation.apply((TContext) host));
        final State<T> state = new BaseState<>(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates an immutable lazy state.
     * <p>
     * {@code factory} defines what the value will be, a holder try to get the value, and the value
     * obtained from there will be the value that will be obtained in subsequent calls to get the
     * value of the state.
     * <pre>{@code
     * State<Integer> intState = lazyState(ThreadLocalRandom.current()::nextInt);
     *
     * intState.get(...); // 54 - from initial computation of random integer ^^
     * intState.get(...); // 54 - previously defined by the initial computation
     * }</pre>
     *
     * @param computation The value factory.
     * @param <T>         The state holder type.
     * @return A lazy state.
     */
    protected final <T> State<T> lazyState(@NotNull Supplier<T> computation) {
        requireNotInitialized();
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new LazyValue(state, computation);
        final State<T> state = new BaseState<>(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates an mutable {@link #lazyState(Function) lazy state} whose value is always computed
     * from the initial data set by its {@link StateValueHost}.
     * <p>
     * When the holder is a {@link IFOpenContext}, the initial value will be the value defined
     * in the initial opening data of the container. This state is specifically set for backwards
     * compatibility with the old way of applying data to a context before or during container open.
     * <p>
     * As to open a view it is necessary to pass a {@link java.util.Map}, the {@code key} is used to
     * get the value from that map.
     *
     * @param key The initial data identifier.
     * @param <T> The initial data value type.
     * @return A state computed with an initial opening data value.
     */
    protected final <T> MutableState<T> initialState(@NotNull String key) {
        requireNotInitialized();
        final long id = State.next();
        final MutableState<T> state =
                new BaseMutableState<>(id, (host, valueState) -> new InitialDataStateValue(valueState, host, key));
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates an mutable {@link #lazyState(Function) lazy state} whose value is always computed
     * from the initial data set by its {@link StateValueHost}.
     * <p>
     * When the holder is a {@code OpenViewContext}, the initial value will be the value defined
     * in the initial opening data of the container. This state is specifically set for backwards
     * compatibility with the old way of applying data to a context before or during container open.
     * <p>
     * The class parameter is used to convert all initial state into a value. Note that support for
     * obtaining a specific value from the initial data is only available from version 2.5.4 of the
     * library.
     *
     * @param <T> The initial data type.
     * @return A state computed with an initial opening data value.
     */
    protected final <T> State<T> initialState() {
        requireNotInitialized();
        final long id = State.next();
        final State<T> state =
                new BaseState<>(id, (host, valueState) -> new InitialDataStateValue(valueState, host, null));
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates a new immutable pagination with static data source.
     *
     * @param sourceProvider The data source for pagination.
     * @param elementConsumer The function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A new immutable pagination state.
     */
    protected final <T> State<Pagination> paginationState(
            @NotNull List<? super T> sourceProvider,
            @NotNull PaginationValueConsumer<TContext, TItem, T> elementConsumer) {
        return this.<T>buildPaginationState(sourceProvider)
                .elementFactory(elementConsumer)
                .build();
    }

    /**
     * Creates a new unmodifiable computed pagination state.
     *
     * @param sourceProvider Data source for pagination.
     * @param valueConsumer  Function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A new unmodifiable pagination state.
     */
    protected final <T> State<Pagination> computedPaginationState(
            @NotNull Function<TContext, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<TContext, TItem, T> valueConsumer) {
        return this.buildComputedPaginationState(sourceProvider)
                .elementFactory(valueConsumer)
                .build();
    }

    /**
     * Creates a new unmodifiable computed pagination state with asynchronous data source.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param sourceProvider The data source for pagination.
     * @param valueConsumer   The function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A new unmodifiable pagination state.
     */
    @ApiStatus.Experimental
    protected final <T> State<Pagination> computedAsyncPaginationState(
            @NotNull Function<TContext, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<TContext, TItem, T> valueConsumer) {
        return this.buildComputedAsyncPaginationState(sourceProvider)
                .elementFactory(valueConsumer)
                .build();
    }

    /**
     * Creates a new unmodifiable lazy pagination state.
     *
     * @param sourceProvider Data source for pagination.
     * @param valueConsumer  Function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A new unmodifiable pagination state.
     */
    protected final <T> State<Pagination> lazyPaginationState(
            @NotNull Function<TContext, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<TContext, TItem, T> valueConsumer) {
        return this.buildLazyPaginationState(sourceProvider)
                .elementFactory(valueConsumer)
                .build();
    }

    /**
     * Creates a new unmodifiable lazy pagination state.
     *
     * @param sourceProvider Data source for pagination.
     * @param valueConsumer  Function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A new unmodifiable pagination state.
     */
    protected final <T> State<Pagination> lazyPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<TContext, TItem, T> valueConsumer) {
        return this.buildLazyPaginationState(sourceProvider)
                .elementFactory(valueConsumer)
                .build();
    }

    /**
     * Creates a new unmodifiable lazy pagination state with asynchronous data source.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param sourceProvider The data source for pagination.
     * @param valueConsumer    The function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A new unmodifiable pagination state.
     */
    @ApiStatus.Experimental
    protected final <T> State<Pagination> lazyAsyncPaginationState(
            @NotNull Function<TContext, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<TContext, TItem, T> valueConsumer) {
        return this.buildLazyAsyncPaginationState(sourceProvider)
                .elementFactory(valueConsumer)
                .build();
    }

    /**
     * Creates a new unmodifiable static pagination state builder.
     *
     * @param sourceProvider The data source for pagination.
     * @param <T>            The pagination data type.
     * @return A new pagination state builder.
     */
    protected final <T> PaginationStateBuilder<TContext, TItem, T> buildPaginationState(
            @NotNull List<? super T> sourceProvider) {
        return new PaginationStateBuilder<>(this, sourceProvider);
    }

    /**
     * Creates a new unmodifiable dynamic pagination state builder.
     *
     * @param sourceProvider The data source for pagination.
     * @param <T>            The pagination data type.
     * @return A new pagination state builder.
     */
    protected final <T> PaginationStateBuilder<TContext, TItem, T> buildComputedPaginationState(
            @NotNull Function<TContext, List<? super T>> sourceProvider) {
        return new PaginationStateBuilder<>(this, sourceProvider, false, true);
    }

    /**
     * Creates a new unmodifiable computed pagination state builder with asynchronous data source.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param sourceProvider The data source for pagination.
     * @param <T>            The pagination data type.
     * @return A new pagination state builder.
     */
    @ApiStatus.Experimental
    protected final <T> PaginationStateBuilder<TContext, TItem, T> buildComputedAsyncPaginationState(
            @NotNull Function<TContext, CompletableFuture<List<T>>> sourceProvider) {
        return new PaginationStateBuilder<>(this, sourceProvider, true, true);
    }

    /**
     * Creates a new unmodifiable lazy pagination state builder.
     *
     * @param sourceProvider The data source for pagination.
     * @param <T>            The pagination data type.
     * @return A new pagination state builder.
     */
    protected final <T> PaginationStateBuilder<TContext, TItem, T> buildLazyPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider) {
        return new PaginationStateBuilder<>(this, sourceProvider, false, false);
    }

    /**
     * Creates a new unmodifiable lazy pagination state builder.
     *
     * @param sourceProvider The data source for pagination.
     * @param <T>            The pagination data type.
     * @return A new pagination state builder.
     */
    protected final <T> PaginationStateBuilder<TContext, TItem, T> buildLazyPaginationState(
            @NotNull Function<TContext, List<? super T>> sourceProvider) {
        return new PaginationStateBuilder<>(this, sourceProvider, false, false);
    }

    /**
     * Creates a new unmodifiable lazy pagination state builder with asynchronous data source.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param sourceProvider The data source for pagination.
     * @param <T>            The pagination data type.
     * @return A new pagination state builder.
     */
    @ApiStatus.Experimental
    protected final <T> PaginationStateBuilder<TContext, TItem, T> buildLazyAsyncPaginationState(
            @NotNull Function<TContext, CompletableFuture<List<T>>> sourceProvider) {
        return new PaginationStateBuilder<>(this, sourceProvider, true, false);
    }

    final <V> State<Pagination> createPaginationState(@NotNull PaginationStateBuilder<TContext, TItem, V> builder) {
        requireNotInitialized();
        final long id = State.next();
        @SuppressWarnings({"unchecked", "rawtypes"})
        final StateValueFactory factory = (host, state) -> new PaginationImpl(
                state,
                (IFContext) host,
                builder.getLayoutTarget(),
                builder.getSourceProvider(),
                (PaginationElementFactory) builder.getElementFactory(),
                (BiConsumer) builder.getPageSwitchHandler(),
                builder.isAsync(),
                builder.isComputed());
        final State<Pagination> state = new PaginationState(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Called when the view is about to be configured, the returned object will be the view's
     * configuration.
     *
     * @param config A ViewConfigBuilder instance to configure this view.
     */
    @ApiStatus.OverrideOnly
    public void onInit(@NotNull ViewConfigBuilder config) {}

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
     * @param open The player view context.
     */
    @ApiStatus.OverrideOnly
    public void onOpen(@NotNull TOpenContext open) {}

    /**
     * Called only once before the container is displayed to a player.
     * <p>
     * The {@code context} is not cancelable, for cancellation use {@link #onOpen(IFOpenContext)} instead.
     * <p>
     * This function should only be used to render items, any external call is completely forbidden
     * as the function runs on the main thread.
     *
     * @param render The rendering context.
     */
    @ApiStatus.OverrideOnly
    public void onFirstRender(@NotNull TRenderContext render) {}

    /**
     * Called when the view is updated for a player.
     *
     * <p>This is a rendering function and can modify the view's inventory.
     *
     * @param update The update context.
     */
    @ApiStatus.OverrideOnly
    public void onUpdate(@NotNull TContext update) {}

    /**
     * Called when the player closes the view's inventory.
     *
     * <p>It is possible to cancel this event and have the view's inventory open again for the player.
     *
     * @param close The player view context.
     */
    @ApiStatus.OverrideOnly
    public void onClose(@NotNull TCloseContext close) {}

    /**
     * Called when an actor clicks on a container while it has a view open.
     * <p>
     * You can know if the click was on entity inventory or view inventory by {@link
     * IFSlotContext#isOnEntityContainer()}
     * Any function that triggers an inventory modification is prohibited from being used in this
     * handler.
     * <p>
     * This context is cancelable and canceling this context will cancel the click, thus canceling
     * all subsequent interceptors causing the pipeline to terminate immediately.
     *
     * @param click The click context.
     */
    @ApiStatus.OverrideOnly
    public void onClick(@NotNull TSlotClickContext click) {}

    /**
     * Called when a context is resumed by {@link IFConfinedContext#back()}.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param origin Who called {@link IFConfinedContext#back() to back}.
     * @param target The context that was back to.
     */
    @ApiStatus.OverrideOnly
    @ApiStatus.Experimental
    public void onResume(@NotNull TContext origin, @NotNull TContext target) {}

    /**
     * Called internally before the first initialization.
     * <p>
     * Use it to register pipeline interceptors.
     *
     * @throws IllegalStateException If this platform view is already initialized.
     */
    @SuppressWarnings("unchecked")
    final void internalInitialization(IFViewFrame<?, ?> framework) {
        if (isInitialized())
            throw new IllegalStateException("Tried to call internal initialization but view is already initialized");

        this.framework = (TFramework) framework;

        final Pipeline<? super VirtualView> pipeline = getPipeline();
        pipeline.intercept(StandardPipelinePhases.INIT, new PlatformInitInterceptor());
        pipeline.intercept(StandardPipelinePhases.OPEN, new PlatformOpenInterceptor());
        pipeline.intercept(StandardPipelinePhases.LAYOUT_RESOLUTION, new LayoutResolutionInterceptor());
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new PlatformRenderInterceptor());
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new LayoutRenderInterceptor());
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new AvailableSlotInterceptor());
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new FirstRenderInterceptor());
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new ScheduledUpdateAfterRenderInterceptor());
        pipeline.intercept(StandardPipelinePhases.UPDATE, new PlatformUpdateHandlerInterceptor());
        pipeline.intercept(StandardPipelinePhases.UPDATE, new UpdateInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLOSE, new PlatformCloseInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLOSE, new ScheduledUpdateAfterCloseInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLICK, new ViewerLastInteractionTrackerInterceptor());
        registerPlatformInterceptors();
        pipeline.execute(StandardPipelinePhases.INIT, this);
    }

    public abstract void registerPlatformInterceptors();

    @ApiStatus.Internal
    public @NotNull ElementFactory getElementFactory() {
        return PlatformUtils.getFactory();
    }
}
