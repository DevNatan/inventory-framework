package me.devnatan.inventoryframework.component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.MutableState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateAccess;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractComponentHandle<CONTEXT, COMPONENT_BUILDER> extends ComponentHandle
        implements StateAccess<CONTEXT, COMPONENT_BUILDER> {

    protected AbstractComponentHandle() {}

    public abstract COMPONENT_BUILDER builder();

    @Override
    public <T> State<T> state(T initialValue) {
        return null;
    }

    @Override
    public <T> MutableState<T> mutableState(T initialValue) {
        return null;
    }

    @Override
    public MutableIntState mutableState(int initialValue) {
        return null;
    }

    @Override
    public <T> State<T> computedState(@NotNull Function<CONTEXT, T> computation) {
        return null;
    }

    @Override
    public <T> State<T> computedState(@NotNull Supplier<T> computation) {
        return null;
    }

    @Override
    public <T> State<T> lazyState(@NotNull Function<CONTEXT, T> computation) {
        return null;
    }

    @Override
    public <T> State<T> lazyState(@NotNull Supplier<T> computation) {
        return null;
    }

    @Override
    public <T> MutableState<T> initialState() {
        return null;
    }

    @Override
    public <T> MutableState<T> initialState(@NotNull String key) {
        return null;
    }

    @Override
    public <T> State<Pagination> paginationState(
            @NotNull List<? super T> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> elementConsumer) {
        return null;
    }

    @Override
    public <T> State<Pagination> computedPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {
        return null;
    }

    @Override
    public <T> State<Pagination> computedAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {
        return null;
    }

    @Override
    public <T> State<Pagination> lazyPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {
        return null;
    }

    @Override
    public <T> State<Pagination> lazyPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {
        return null;
    }

    @Override
    public <T> State<Pagination> lazyAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider,
            @NotNull PaginationValueConsumer<CONTEXT, COMPONENT_BUILDER, T> valueConsumer) {
        return null;
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildPaginationState(
            @NotNull List<? super T> sourceProvider) {
        return null;
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildComputedPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider) {
        return null;
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildComputedAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider) {
        return null;
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildLazyPaginationState(
            @NotNull Supplier<List<? super T>> sourceProvider) {
        return null;
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildLazyPaginationState(
            @NotNull Function<CONTEXT, List<? super T>> sourceProvider) {
        return null;
    }

    @Override
    public <T> PaginationBuilder<CONTEXT, COMPONENT_BUILDER, T> buildLazyAsyncPaginationState(
            @NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider) {
        return null;
    }
}
