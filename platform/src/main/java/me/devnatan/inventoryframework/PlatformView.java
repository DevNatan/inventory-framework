package me.devnatan.inventoryframework;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.PlatformUtils;
import me.devnatan.inventoryframework.pagination.Pagination;
import me.devnatan.inventoryframework.pagination.PaginationImpl;
import me.devnatan.inventoryframework.pipeline.InitInterceptor;
import me.devnatan.inventoryframework.pipeline.OpenInterceptor;
import me.devnatan.inventoryframework.pipeline.RenderInterceptor;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import me.devnatan.inventoryframework.state.ImmutableValue;
import me.devnatan.inventoryframework.state.MutableState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateHost;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class PlatformView<
                TItem extends IFItem<TItem>,
                TContext extends IFContext,
                TOpenContext extends IFOpenContext,
                TCloseContext extends IFCloseContext,
                TRenderContext extends IFRenderContext<TItem>,
                TSlotContext extends IFSlotContext,
                TSlotClickContext extends IFSlotClickContext>
        extends DefaultRootView {

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
        return new ViewConfigBuilder();
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
     * @param <R>         The state type and computation return value.
     * @return A lazy state.
     */
    protected final <R> State<R> state(@NotNull Function<TContext, R> computation) {
        throw new UnsupportedOperationException();
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
     * @param <V>         The state value type.
     * @return An immutable computed state.
     */
    protected final <V> State<V> computedState(@NotNull Function<TContext, V> computation) {
        throw new UnsupportedOperationException();
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
     * @param <V>         The state holder type.
     * @return An immutable computed state.
     */
    protected final <V> State<V> computedState(@NotNull Supplier<V> computation) {
        throw new UnsupportedOperationException();
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
     * @param <V>         The state value type.
     * @return A lazy state.
     */
    protected final <V> State<V> lazyState(@NotNull Function<TContext, V> computation) {
        throw new UnsupportedOperationException();
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
     * @param <V>         The state holder type.
     * @return A lazy state.
     */
    protected final <V> State<V> lazyState(@NotNull Supplier<V> computation) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates an immutable {@link #lazyState(Function) lazy state} whose value is always computed
     * from the initial data set by its {@link StateHost}.
     * <p>
     * When the holder is a {@link IFOpenContext}, the initial value will be the value defined
     * in the initial opening data of the container. This state is specifically set for backwards
     * compatibility with the old way of applying data to a context before or during container open.
     * <p>
     * As to open a view it is necessary to pass a {@link java.util.Map}, the {@code key} is used to
     * get the value from that map.
     *
     * @param key The initial data identifier.
     * @param <V> The initial data value type.
     * @return A state computed with an initial opening data value.
     */
    protected final <V> State<V> initialState(@NotNull String key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates an immutable {@link #lazyState(Function) lazy state} whose value is always computed
     * from the initial data set by its {@link StateHost}.
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
        throw new UnsupportedOperationException();
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
     * @param <V>          The state value type.
     * @return A mutable state with an initial value.
     */
    protected final <V> MutableState<V> mutable(V initialValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a {@link MutableState mutable state} with an initial {@code int} value.
     *
     * @param initialValue The initial value of the state.
     * @return A mutable state with an initial {@code int} value.
     */
    protected final MutableState<Integer> mutableInt(int initialValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a {@link MutableState mutable state} with an initial {@code int} value of {@code 0}.
     *
     * @return A mutable state with an initial {@code int} value of {@code 0}.
     */
    protected final MutableState<Integer> mutableInt() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates an immutable state used to control the pagination.
     * <p>
     * How each paginated element will be rendered is determined in the {@code itemFactory}, that
     * is called every time a paginated element is rendered in the context container.
     * <pre>{@code
     * State<Pagination> pagination = pagination(
     *     (item, value) -> item.withItem(...)
     * )
     *
     * }</pre>
     * <p>
     * Control and get pagination info by accessing the state.
     * <pre>{@code
     * Pagination ctxPagination = pagination.get(ctx);
     * int currentPage = ctxPagination.currentPage();
     *
     * // Advances the pagination for the selected context
     * ctxPagination.advance();
     * }</pre>
     * <p>
     * Asynchronous pagination can be done using a {@link CompletableFuture} as {@code sourceProvider}.
     * <pre>{@code
     * Pagination pagination = pagination(
     *     () -> getCompletedFutureSomehow(),
     *     (item, value) -> item.withItem(...)
     * )
     *
     * }</pre>
     *
     * @param sourceProvider The data provider for pagination.
     * @param itemFactory    The function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <V>            The pagination data type.
     * @return A immutable pagination state.
     */
    @SuppressWarnings("unchecked")
    protected final <V> State<Pagination> pagination(
            @NotNull Function<TSlotContext, List<? super V>> sourceProvider,
            @NotNull BiConsumer<TItem, V> itemFactory) {
        return stateFactory.createState(new ImmutableValue(new PaginationImpl(
                this,
                null /* TODO */,
                (Function<IFSlotContext, List<?>>) sourceProvider,
                (BiConsumer<IFItem<?>, Object>) itemFactory)));
    }

    /**
     * Creates an immutable state used to control the pagination.
     * <p>
     * How each paginated element will be rendered is determined by the {@code itemFactory} parameter,
     * that is called every time a paginated element is rendered in the context container.
     * <pre>{@code
     * State<Pagination> pagination = pagination(
     *     (item, value) -> item.withItem(...)
     * )}</pre>
     * <p>
     * Control and get pagination info by accessing the state.
     * <pre>{@code
     * Pagination ctxPagination = pagination.get(ctx);
     * int currentPage = ctxPagination.currentPage();
     *
     * // Advances the pagination for the selected context
     * ctxPagination.advance();
     * }</pre>
     * <p>
     * Asynchronous pagination can be done using a {@link CompletableFuture} as {@code sourceProvider}.
     * <pre>{@code
     * State<Pagination> pagination = pagination(
     *     () -> getCompletedFutureSomehow(),
     *     (item, value) -> item.withItem(...)
     * )
     *
     * }</pre>
     *
     * @param sourceProvider The data provider for pagination.
     * @param itemFactory    The function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <V>            The pagination data type.
     * @return A immutable pagination state.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final <V> State<Pagination> pagination(
            @NotNull Supplier<List<V>> sourceProvider, @NotNull BiConsumer<TItem, V> itemFactory) {
        return pagination((Function<TSlotContext, List<? super V>>) $ -> sourceProvider.get(), itemFactory);
    }

    /**
     * Creates an immutable state used to control the pagination.
     * <p>
     * How each paginated element will be rendered is determined in the {@code itemFactory}, that
     * is called every time a paginated element is rendered in the context container.
     * <pre>{@code
     * State<Pagination> pagination = pagination(
     *     (item, value) -> item.withItem(...)
     * )
     *
     * }</pre>
     * <p>
     * Control and get pagination info by accessing the state.
     * <pre>{@code
     * Pagination ctxPagination = ctxPagination.get(ctx);
     * int currentPage = ctxPagination.currentPage();
     *
     * // Advances the pagination for the selected context
     * ctxPagination.advance();
     * }</pre>
     * <p>
     * Asynchronous pagination can be done using a {@link CompletableFuture} as {@code sourceProvider}.
     * <pre>{@code
     * State<Pagination> pagination = pagination(
     *     () -> getCompletedFutureSomehow(),
     *     (item, value) -> item.withItem(...)
     * )
     *
     * }</pre>
     *
     * @param sourceProvider The data provider for pagination.
     * @param itemFactory    The function for creating pagination items, this function is called for
     *                       each paged element (item) on a page.
     * @param <T>            The pagination data type.
     * @return A immutable pagination state.
     */
    protected final <T> State<Pagination> pagination(
            @NotNull State<List<T>> sourceProvider, @NotNull BiConsumer<TItem, T> itemFactory) {
        throw new UnsupportedOperationException("Pagination using state is not yet supported");
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
     * @param ctx The player view context.
     */
    @ApiStatus.OverrideOnly
    public void onOpen(TOpenContext ctx) {}

    /**
     * Called only once before the container is displayed to a player.
     * <p>
     * The {@code context} is not cancelable, for cancellation use {@link #onOpen(IFOpenContext)} instead.
     * <p>
     * This function should only be used to render items, any external call is completely forbidden
     * as the function runs on the main thread.
     *
     * @param ctx The renderization context.
     */
    @ApiStatus.OverrideOnly
    public void onFirstRender(TRenderContext ctx) {}

    /**
     * Called when the view is updated for a player.
     *
     * <p>This is a rendering function and can modify the view's inventory.
     *
     * @param ctx The player view context.
     */
    @ApiStatus.OverrideOnly
    public void onUpdate(TContext ctx) {}

    /**
     * Called when the player closes the view's inventory.
     *
     * <p>It is possible to cancel this event and have the view's inventory open again for the player.
     *
     * @param ctx The player view context.
     */
    @ApiStatus.OverrideOnly
    public void onClose(TCloseContext ctx) {}

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
     * @param ctx The click context.
     */
    @ApiStatus.OverrideOnly
    public void onClick(TSlotClickContext ctx) {}

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
    void internalInitialization() {
        if (isInitialized())
            throw new IllegalStateException("Tried to call internal initialization but view is already initialized");

        getPipeline().intercept(StandardPipelinePhases.INIT, new InitInterceptor());
        getPipeline().intercept(StandardPipelinePhases.OPEN, new OpenInterceptor());
        getPipeline().intercept(StandardPipelinePhases.RENDER, new RenderInterceptor());
        getElementFactory().registerPlatformInterceptors(this);
        getPipeline().execute(StandardPipelinePhases.INIT, this);
    }

    @ApiStatus.Internal
    public ElementFactory getElementFactory() {
        return PlatformUtils.getFactory();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void open(@NotNull Viewer viewer) {
        if (!isInitialized()) throw new IllegalStateException("Cannot open a uninitialized view");

        final IFOpenContext context =
                getElementFactory().createContext(this, null, viewer, IFOpenContext.class, false, null);
        context.addViewer(viewer);

        // TODO assign inherited state
        //        for (final State<?> inheritedState : inheritableStates)
        //			context.getStateHost().
        //            context.getStateHost().onOpen((TOpenContext) context);

        getPipeline().execute(StandardPipelinePhases.OPEN, context);
    }
}
