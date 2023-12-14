package me.devnatan.inventoryframework.state;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.component.PaginationBuilder;
import me.devnatan.inventoryframework.component.PaginationImpl;
import me.devnatan.inventoryframework.component.PaginationValueConsumer;
import me.devnatan.inventoryframework.context.IFRenderContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class StateAccessImpl<CONTEXT, ITEM_BUILDER> implements StateAccess<CONTEXT, ITEM_BUILDER> {

    private final VirtualView caller;
    private final StateRegistry stateRegistry;

    public StateAccessImpl(VirtualView caller, StateRegistry stateRegistry) {
        this.caller = caller;
        this.stateRegistry = stateRegistry;
    }

    @Override
    public <T> State<T> state(T initialValue) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new ImmutableValue(id, initialValue);
        final State<T> state = new BaseState<>(id, factory);
        stateRegistry.registerState(state, this);

        return state;
    }

    @Override
    public <T> MutableState<T> mutableState(T initialValue) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new MutableValue(id, initialValue);
        final MutableState<T> state = new MutableGenericStateImpl<>(id, factory);
        this.stateRegistry.registerState(state, this);

        return state;
    }

    @Override
    public MutableIntState mutableState(int initialValue) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new MutableValue(id, initialValue);
        final MutableIntState state = new MutableIntStateImpl(id, factory);
        this.stateRegistry.registerState(state, this);

        return state;
    }

    @Override
    public <T> State<T> computedState(@NotNull Function<CONTEXT, T> computation) {
        final long id = State.next();
        @SuppressWarnings("unchecked")
        final StateValueFactory factory =
                (host, state) -> new ComputedValue(id, () -> computation.apply((CONTEXT) host));
        final State<T> state = new BaseState<>(id, factory);
        this.stateRegistry.registerState(state, this);

        return state;
    }

    @Override
    public <T> State<T> computedState(@NotNull Supplier<T> computation) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new ComputedValue(id, computation);
        final State<T> state = new BaseState<>(id, factory);
        this.stateRegistry.registerState(state, this);

        return state;
    }

    @Override
    public <T> State<T> lazyState(@NotNull Function<CONTEXT, T> computation) {
        final long id = State.next();
        @SuppressWarnings("unchecked")
        final StateValueFactory factory = (host, state) -> new LazyValue(id, () -> computation.apply((CONTEXT) host));
        final State<T> state = new BaseState<>(id, factory);
        this.stateRegistry.registerState(state, this);

        return state;
    }

    @Override
    public <T> State<T> lazyState(@NotNull Supplier<T> computation) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new LazyValue(id, computation);
        final State<T> state = new BaseState<>(id, factory);
        this.stateRegistry.registerState(state, this);

        return state;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public <T> MutableState<T> initialState() {
        return initialState(null);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public <T> MutableState<T> initialState(@NotNull String key) {
        final long id = State.next();
        final MutableState<T> state =
                new BaseMutableState<>(id, (host, valueState) -> new InitialDataStateValue(id, host, key));
        this.stateRegistry.registerState(state, this);

        return state;
    }

    @Override
    public <T> State<Pagination> paginationState(
            @NotNull List<? super T> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> elementConsumer) {
        return createPaginationState(
                this.<T>buildPaginationState(sourceProvider).elementFactory(elementConsumer));
    }

    @Override
    public <T> State<Pagination> computedPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
        return createPaginationState(
                this.buildComputedPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    @Override
    public <T> State<Pagination> computedAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
        return createPaginationState(
                this.buildComputedAsyncPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    @Override
    public <T> State<Pagination> lazyPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
        return createPaginationState(buildLazyPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    @Override
    public <T> State<Pagination> lazyPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
        return createPaginationState(buildLazyPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    @Override
    public <T> State<Pagination> lazyAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
        return createPaginationState(
                buildLazyAsyncPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, ITEM_BUILDER, T> buildPaginationState(
            @NotNull List<? super T> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, false, false, this::createPaginationState);
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, ITEM_BUILDER, T> buildComputedPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, false, true, this::createPaginationState);
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, ITEM_BUILDER, T> buildComputedAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, true, true, this::createPaginationState);
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, ITEM_BUILDER, T> buildLazyPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, false, false, this::createPaginationState);
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, ITEM_BUILDER, T> buildLazyPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, false, false, this::createPaginationState);
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, ITEM_BUILDER, T> buildLazyAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, true, false, this::createPaginationState);
    }

    <V> State<Pagination> createPaginationState(@NotNull PaginationBuilder<CONTEXT, ITEM_BUILDER, V> builder) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> (PaginationImpl)
                builder.withSelfManaged(!(caller instanceof IFRenderContext)).buildComponent0(id, (VirtualView) host);
        final State<Pagination> state = new PaginationState(id, factory);
        this.stateRegistry.registerState(state, caller);

        return state;
    }
}
