package me.devnatan.inventoryframework.component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.MutableState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateAccess;
import me.devnatan.inventoryframework.state.StateAccessImpl;
import me.devnatan.inventoryframework.state.StateRegistry;
import org.jetbrains.annotations.NotNull;

public abstract class PlatformComponentHandle<CONTEXT, COMPONENT_BUILDER> extends ComponentHandle
        implements StateAccess<CONTEXT, COMPONENT_BUILDER>, VirtualView {

    private final StateRegistry stateRegistry = new StateRegistry();
    private final StateAccess<CONTEXT, COMPONENT_BUILDER> stateAccess = new StateAccessImpl<>(this, stateRegistry);

    protected PlatformComponentHandle() {}

    public abstract COMPONENT_BUILDER builder();

    @Override
    public final <T> State<T> state(T initialValue) {
        return stateAccess.state(initialValue);
    }

    @Override
    public final <T> MutableState<T> mutableState(T initialValue) {
        return stateAccess.mutableState(initialValue);
    }

    @Override
    public final MutableIntState mutableState(int initialValue) {
        return stateAccess.mutableState(initialValue);
    }

    @Override
    public final <T> State<T> computedState(@NotNull Function<CONTEXT, T> computation) {
        return stateAccess.computedState(computation);
    }

    @Override
    public final <T> State<T> computedState(@NotNull Supplier<T> computation) {
        return stateAccess.computedState(computation);
    }

    @Override
    public final <T> State<T> lazyState(@NotNull Function<CONTEXT, T> computation) {
        return stateAccess.lazyState(computation);
    }

    @Override
    public final <T> State<T> lazyState(@NotNull Supplier<T> computation) {
        return stateAccess.lazyState(computation);
    }

    @Override
    public final <T> MutableState<T> initialState() {
        return stateAccess.initialState();
    }

    @Override
    public final <T> MutableState<T> initialState(@NotNull String key) {
        return stateAccess.initialState(key);
    }

    @Override
    public final <T> State<Pagination> paginationState(
            @NotNull List<? super T> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> elementConsumer) {
        return stateAccess.paginationState(sourceProvider, elementConsumer);
    }

    @Override
    public final <T> State<Pagination> computedPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {

        return stateAccess.computedPaginationState(sourceProvider, valueConsumer);
    }

    @Override
    public final <T> State<Pagination> computedAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {
        return stateAccess.computedAsyncPaginationState(sourceProvider, valueConsumer);
    }

    @Override
    public final <T> State<Pagination> lazyPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {

        return stateAccess.lazyPaginationState(sourceProvider, valueConsumer);
    }

    @Override
    public final <T> State<Pagination> lazyPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {
        return stateAccess.lazyPaginationState(sourceProvider, valueConsumer);
    }

    @Override
    public final <T> State<Pagination> lazyAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {

        return stateAccess.lazyAsyncPaginationState(sourceProvider, valueConsumer);
    }

    @Override
    public final <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildPaginationState(
            @NotNull List<? super T> sourceProvider) {
        return stateAccess.buildPaginationState(sourceProvider);
    }

    @Override
    public final <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildComputedPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider) {
        return stateAccess.buildComputedPaginationState(sourceProvider);
    }

    @Override
    public final <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildComputedAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider) {
        return stateAccess.buildComputedAsyncPaginationState(sourceProvider);
    }

    @Override
    public final <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildLazyPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider) {
        return stateAccess.buildLazyPaginationState(sourceProvider);
    }

    @Override
    public final <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildLazyPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider) {
        return stateAccess.buildLazyPaginationState(sourceProvider);
    }

    @Override
    public final <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildLazyAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider) {
        return stateAccess.buildLazyAsyncPaginationState(sourceProvider);
    }
    // endregion
}
