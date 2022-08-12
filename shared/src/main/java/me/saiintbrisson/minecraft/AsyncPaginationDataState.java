package me.saiintbrisson.minecraft;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;

/**
 * Asynchronous paging state machine data class.
 *
 * <p>All contexts executed for this state machine are temporary, that is, any item that is set to
 * the container from them will cease to exist once the event is discarded.
 *
 * @param <T> The pagination data type.
 */
@Getter
@RequiredArgsConstructor
public final class AsyncPaginationDataState<T> {

    private final Function<PaginatedViewContext<T>, CompletableFuture<List<T>>> job;

    private Consumer<PaginatedViewContext<T>> loadStarted, loadFinished, success;
    private BiConsumer<PaginatedViewContext<T>, Throwable> error;

    /**
     * Called when pagination data starts to load.
     *
     * @param handler The load handler.
     * @return This async pagination data state.
     */
    @Contract(mutates = "this")
    public AsyncPaginationDataState<T> onStart(Consumer<PaginatedViewContext<T>> handler) {
        this.loadStarted = handler;
        return this;
    }

    /**
     * Called when the pagination data successfully loads.
     *
     * @param handler The success handler.
     * @return This async pagination data state.
     */
    @Contract(mutates = "this")
    public AsyncPaginationDataState<T> onSuccess(Consumer<PaginatedViewContext<T>> handler) {
        this.success = handler;
        return this;
    }

    /**
     * Called when an error occurs while loading paging data.
     *
     * @param handler The success handler.
     * @return This async pagination data state.
     */
    @Contract(mutates = "this")
    public AsyncPaginationDataState<T> onError(BiConsumer<PaginatedViewContext<T>, Throwable> handler) {
        this.error = handler;
        return this;
    }

    /**
     * Called when the entire loading process is finished (even if there is an error).
     *
     * @param handler The success handler.
     * @return This async pagination data state.
     */
    @Contract(mutates = "this")
    public AsyncPaginationDataState<T> onFinish(Consumer<PaginatedViewContext<T>> handler) {
        this.loadFinished = handler;
        return this;
    }
}
