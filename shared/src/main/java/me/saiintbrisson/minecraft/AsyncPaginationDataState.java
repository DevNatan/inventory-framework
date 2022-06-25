package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Asynchronous pagination data state holder.
 *
 * @param <T> The pagination data type.
 */
@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class AsyncPaginationDataState<T> {

	final CompletableFuture<T> job;

	private Consumer<PaginatedViewContext<T>> loadHandler, doneHandler;
	private BiConsumer<PaginatedViewContext<T>, Throwable> errorHandler;
	private BiConsumer<PaginatedViewContext<T>, T> completeHandler;

	/**
	 * Called when pagination data starts to load.
	 *
	 * @param handler The load handler.
	 * @return This async pagination data state.
	 */
	@Contract(mutates = "this")
	public AsyncPaginationDataState<T> onLoad(Consumer<PaginatedViewContext<T>> handler) {
		this.loadHandler = handler;
		return this;
	}

	/**
	 * Called when the pagination data successfully loads.
	 *
	 * @param handler The success handler.
	 * @return This async pagination data state.
	 */
	@Contract(mutates = "this")
	public AsyncPaginationDataState<T> onSuccess(BiConsumer<PaginatedViewContext<T>, T> handler) {
		this.completeHandler = handler;
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
		this.errorHandler = handler;
		return this;
	}

	/**
	 * Called when the entire loading process is finished (even if there is an error).
	 *
	 * @param handler The success handler.
	 * @return This async pagination data state.
	 */
	@Contract(mutates = "this")
	public AsyncPaginationDataState<T> onDone(Consumer<PaginatedViewContext<T>> handler) {
		this.doneHandler = handler;
		return this;
	}
}
