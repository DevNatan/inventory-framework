package me.devnatan.inventoryframework;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.component.PaginationElementFactory;
import me.devnatan.inventoryframework.component.PaginationImpl;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.PlatformUtils;
import me.devnatan.inventoryframework.pipeline.AvailableSlotInterceptor;
import me.devnatan.inventoryframework.pipeline.CloseInterceptor;
import me.devnatan.inventoryframework.pipeline.FirstRenderInterceptor;
import me.devnatan.inventoryframework.pipeline.InitInterceptor;
import me.devnatan.inventoryframework.pipeline.LayoutInterceptor;
import me.devnatan.inventoryframework.pipeline.OpenInterceptor;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import me.devnatan.inventoryframework.pipeline.UpdateInterceptor;
import me.devnatan.inventoryframework.state.BaseState;
import me.devnatan.inventoryframework.state.ComputedValue;
import me.devnatan.inventoryframework.state.ImmutableValue;
import me.devnatan.inventoryframework.state.InitialDataStateValue;
import me.devnatan.inventoryframework.state.LazyValue;
import me.devnatan.inventoryframework.state.MutableState;
import me.devnatan.inventoryframework.state.MutableStateImpl;
import me.devnatan.inventoryframework.state.MutableValue;
import me.devnatan.inventoryframework.state.PaginationState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValueFactory;
import me.devnatan.inventoryframework.state.StateValueHost;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class PlatformView<
                TItem extends ItemComponentBuilder<TItem> & ComponentFactory,
                TContext extends IFContext,
                TOpenContext extends IFOpenContext,
                TCloseContext extends IFCloseContext,
                TRenderContext extends IFRenderContext,
                TSlotContext extends IFSlotContext,
                TSlotClickContext extends IFSlotClickContext>
        extends DefaultRootView {

    @Getter(AccessLevel.PACKAGE)
    private IFViewFrame<?> framework;

    private boolean initialized;

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.OverrideOnly
    public void onInit(ViewConfigBuilder config) {}

    /**
     * Creates a new configuration builder.
     *
     * @return A new {@link ViewConfigBuilder} instance.
     */
    @NotNull
    public final ViewConfigBuilder createConfig() {
        return new ViewConfigBuilder().type(ViewType.CHEST);
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
     * MutableState<Integer> intState = mutableState(0);
     *
     * intState.get(...); // 0
     * intState.set(4, ...);
     * intState.get(...); // 4
     * }</pre>
     *
     * @param initialValue The initial value of the state.
     * @param <T>          The state value type.
     * @return A mutable state with an initial value.
     */
    protected final <T> MutableState<T> mutableState(T initialValue) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new MutableValue(state, initialValue);
        final MutableState<T> state = new MutableStateImpl<>(id, factory);
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
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new LazyValue(state, computation);
        final State<T> state = new BaseState<>(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates an immutable {@link #lazyState(Function) lazy state} whose value is always computed
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
    protected final <T> State<T> initialState(@NotNull String key) {
        // TODO missing use of `key`
        final long id = State.next();
        final State<T> state = new BaseState<>(id, (host, valueState) -> new InitialDataStateValue(valueState, host));
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates an immutable {@link #lazyState(Function) lazy state} whose value is always computed
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
     * @param stateClassType The initial data class type.
     * @param <T>            The initial data type.
     * @return A state computed with an initial opening data value.
     */
    protected final <T> State<T> initialState(@NotNull Class<? extends T> stateClassType) {
        // TODO missing use of `stateClassType`
        final long id = State.next();
        final State<T> state = new BaseState<>(id, (host, valueState) -> new InitialDataStateValue(valueState, host));
        stateRegistry.registerState(state, this);

        return state;
    }

    /**
     * Creates an immutable state used to control the pagination.
     * <p>
     * How each paginated element will be rendered is determined by the {@code itemFactory} parameter,
     * that is called every time a paginated element is rendered in the context container.
     * <pre>{@code
     * State<Pagination> paginationState = paginationState(
     *     new ArrayList<>(),
     *     (item, value) -> item.withItem(...)
     * )}</pre>
     *
     * @param sourceProvider The data provider for pagination.
     * @param itemFactory    The function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A immutable pagination state.
     */
    protected final <T> State<Pagination> paginationState(
            @NotNull List<? super T> sourceProvider, @NotNull BiConsumer<TItem, T> itemFactory) {
        return this.<T>buildPaginationState(sourceProvider)
                .itemFactory(itemFactory)
                .build();
    }

    /**
     * Creates an immutable state used to control the pagination.
     * <p>
     * How each paginated element will be rendered is determined in the {@code itemFactory}, that
     * is called every time a paginated element is rendered in the context container.
     * <pre>{@code
     * State<Pagination> paginationState = pagination(
     *     (context) -> new ArrayList<>(),
     *     (item, value) -> item.withItem(...)
     * )}</pre>
     *
     * @param sourceProvider The data provider for pagination.
     * @param itemFactory    The function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A immutable pagination state.
     */
    protected final <T> State<Pagination> paginationState(
            @NotNull Function<TSlotContext, List<? super T>> sourceProvider,
            @NotNull BiConsumer<TItem, T> itemFactory) {
        return this.buildPaginationState(sourceProvider)
                .itemFactory(itemFactory)
                .build();
    }

    /**
     * Creates an immutable state used to control the pagination.
     * <p>
     * How each paginated element will be rendered is determined by the {@code itemFactory} parameter,
     * that is called every time a paginated element is rendered in the context container.
     * <pre>{@code
     * State<Pagination> paginationState = paginationState(
     *     () -> new ArrayList<>(),
     *     (item, value) -> item.withItem(...)
     * )}</pre>
     *
     * @param sourceProvider The data provider for pagination.
     * @param itemFactory    The function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A immutable pagination state.
     */
    protected final <T> State<Pagination> paginationState(
            @NotNull Supplier<List<? super T>> sourceProvider, @NotNull BiConsumer<TItem, T> itemFactory) {
        return this.<T>buildPaginationState(sourceProvider)
                .itemFactory(itemFactory)
                .build();
    }

    @SuppressWarnings("unchecked")
    protected final <T> PaginationStateBuilder<TSlotClickContext, TItem, T> buildPaginationState(
            @NotNull List<? super T> sourceProvider) {
        return new PaginationStateBuilder<>(
                (PlatformView<TItem, ?, ?, ?, ?, TSlotClickContext, ?>) this, sourceProvider);
    }

    @SuppressWarnings("unchecked")
    protected final <T> PaginationStateBuilder<TSlotClickContext, TItem, T> buildPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider) {
        return new PaginationStateBuilder<>(
                (PlatformView<TItem, ?, ?, ?, ?, TSlotClickContext, ?>) this, sourceProvider);
    }

    @SuppressWarnings("unchecked")
    protected final <T> PaginationStateBuilder<TSlotClickContext, TItem, T> buildPaginationState(
            @NotNull Function<TSlotContext, List<? super T>> sourceProvider) {
        return new PaginationStateBuilder<>(
                (PlatformView<TItem, ?, ?, ?, ?, TSlotClickContext, ?>) this, sourceProvider);
    }

    final <V> State<Pagination> buildPaginationState(@NotNull PaginationStateBuilder<TSlotContext, TItem, V> builder) {
        final long id = State.next();
        @SuppressWarnings("unchecked")
        final StateValueFactory factory = (host, state) -> new PaginationImpl(
                state,
                (TContext) host,
                builder.getLayoutTarget(),
                builder.getSourceProvider(),
                (PaginationElementFactory<Object>) builder.getElementFactory(),
                builder.getPageSwitchHandler());
        final State<Pagination> state = new PaginationState(id, factory);
        stateRegistry.registerState(state, this);

        return state;
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
     * @param open The player view context.
     */
    @ApiStatus.OverrideOnly
    public void onOpen(TOpenContext open) {}

    /**
     * Called only once before the container is displayed to a player.
     * <p>
     * The {@code context} is not cancelable, for cancellation use {@link #onOpen(IFOpenContext)} instead.
     * <p>
     * This function should only be used to render items, any external call is completely forbidden
     * as the function runs on the main thread.
     *
     * @param render The renderization context.
     */
    @ApiStatus.OverrideOnly
    public void onFirstRender(TRenderContext render) {}

    /**
     * Called when the view is updated for a player.
     *
     * <p>This is a rendering function and can modify the view's inventory.
     *
     * @param update The player view context.
     */
    @ApiStatus.OverrideOnly
    public void onUpdate(TContext update) {}

    /**
     * Called when the player closes the view's inventory.
     *
     * <p>It is possible to cancel this event and have the view's inventory open again for the player.
     *
     * @param close The player view context.
     */
    @ApiStatus.OverrideOnly
    public void onClose(TCloseContext close) {}

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
    public void onClick(TSlotClickContext click) {}

    /**
     * Initialization state of this view.
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
     * Called internally before the first initialization.
     * <p>
     * Use it to register pipeline interceptors.
     *
     * @throws IllegalStateException If this platform view is already initialized.
     */
    final void internalInitialization(IFViewFrame<?> framework) {
        if (isInitialized())
            throw new IllegalStateException("Tried to call internal initialization but view is already initialized");

        this.framework = framework;

        final Pipeline<? super VirtualView> pipeline = getPipeline();
        pipeline.intercept(StandardPipelinePhases.INIT, new InitInterceptor());
        pipeline.intercept(StandardPipelinePhases.OPEN, new OpenInterceptor());
        pipeline.intercept(StandardPipelinePhases.LAYOUT_RESOLUTION, new LayoutInterceptor());
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new AvailableSlotInterceptor());
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new FirstRenderInterceptor());
        pipeline.intercept(StandardPipelinePhases.UPDATE, new UpdateInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLOSE, new CloseInterceptor());
        registerPlatformInterceptors();
        pipeline.execute(StandardPipelinePhases.INIT, this);
    }

    public abstract void registerPlatformInterceptors();

    @ApiStatus.Internal
    public @NotNull ElementFactory getElementFactory() {
        return PlatformUtils.getFactory();
    }

    @Override
    public final void open(@NotNull Viewer viewer) {
        if (!isInitialized()) throw new IllegalStateException("Cannot open a uninitialized view");

        final IFOpenContext context =
                getElementFactory().createContext(this, null, viewer, IFOpenContext.class, false, null);
        context.addViewer(viewer);
        getPipeline().execute(StandardPipelinePhases.OPEN, context);
    }
}
