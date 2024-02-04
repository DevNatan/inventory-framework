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
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class PlatformStateAccessImpl<C extends IFContext, B extends PlatformComponentBuilder<B, C>>
        implements StateAccess<C> {

    private final VirtualView caller;
    private final StateRegistry stateRegistry;

    public PlatformStateAccessImpl(VirtualView caller, StateRegistry stateRegistry) {
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
    public <T> State<T> computedState(@NotNull Function<C, T> computation) {
        final long id = State.next();
        @SuppressWarnings("unchecked")
        final StateValueFactory factory = (host, state) -> new ComputedValue(id, () -> computation.apply((C) host));
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
    public <T> State<T> lazyState(@NotNull Function<C, T> computation) {
        final long id = State.next();
        @SuppressWarnings("unchecked")
        final StateValueFactory factory = (host, state) -> new LazyValue(id, () -> computation.apply((C) host));
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

    @Override
    public <T> MutableState<T> initialState(@UnknownNullability String key) {
        final long id = State.next();
        final MutableState<T> state =
                new BaseMutableState<>(id, (host, valueState) -> new InitialDataStateValue(id, host, key));
        this.stateRegistry.registerState(state, this);

        return state;
    }

    public <T> State<Pagination> paginationState(
            @NotNull List<? super T> sourceProvider, @NotNull PaginationValueConsumer<C, B, T> elementConsumer) {
        return createPaginationState(
                this.<T>buildPaginationState(sourceProvider).elementFactory(elementConsumer));
    }

    public <T> State<Pagination> computedPaginationState(
            @NotNull Function<C, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<C, B, T> valueConsumer) {
        return createPaginationState(
                this.buildComputedPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    public <T> State<Pagination> computedAsyncPaginationState(
            @NotNull Function<C, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<C, B, T> valueConsumer) {
        return createPaginationState(
                this.buildComputedAsyncPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    public <T> State<Pagination> lazyPaginationState(
            @NotNull Function<C, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<C, B, T> valueConsumer) {
        return createPaginationState(buildLazyPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    public <T> State<Pagination> lazyPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<C, B, T> valueConsumer) {
        return createPaginationState(buildLazyPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    public <T> State<Pagination> lazyAsyncPaginationState(
            @NotNull Function<C, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<C, B, T> valueConsumer) {
        return createPaginationState(
                buildLazyAsyncPaginationState(sourceProvider).elementFactory(valueConsumer));
    }

    public <T> PaginationBuilder<C, B, T> buildPaginationState(@NotNull List<? super T> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, false, false, this::createPaginationState);
    }

    public <T> PaginationBuilder<C, B, T> buildComputedPaginationState(
            @NotNull Function<C, List<? super T>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, false, true, this::createPaginationState);
    }

    public <T> PaginationBuilder<C, B, T> buildComputedAsyncPaginationState(
            @NotNull Function<C, CompletableFuture<List<T>>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, true, true, this::createPaginationState);
    }

    public <T> PaginationBuilder<C, B, T> buildLazyPaginationState(@NotNull Supplier<List<? super T>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, false, false, this::createPaginationState);
    }

    public <T> PaginationBuilder<C, B, T> buildLazyPaginationState(
            @NotNull Function<C, List<? super T>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, false, false, this::createPaginationState);
    }

    public <T> PaginationBuilder<C, B, T> buildLazyAsyncPaginationState(
            @NotNull Function<C, CompletableFuture<List<T>>> sourceProvider) {
        return new PaginationBuilder<>(sourceProvider, true, false, this::createPaginationState);
    }

    <V> State<Pagination> createPaginationState(@NotNull PaginationBuilder<C, B, V> builder) {
        final long id = State.next();
        final boolean isCreatedByUser = !(caller instanceof IFRenderContext);
        final StateValueFactory factory = (host, state) ->
                (PaginationImpl) builder.withSelfManaged(isCreatedByUser).buildComponent0(id);
        final State<Pagination> state = new BaseState<>(id, factory);
        this.stateRegistry.registerState(state, caller);

        return state;
    }
}
