package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Asynchronous paging state machine data class.
 * <p>
 * All contexts executed for this state machine are temporary, that is, any item that is set to the
 * container from them will cease to exist once the event is discarded.
 *
 * @param <T> The pagination data type.
 */
@Getter(AccessLevel.PACKAGE)
@ToString
public final class PaginationStateMachineData<T> {

	private Consumer<PaginatedViewContext<T>> loadStarted, loadFinished;
	private Consumer<PaginatedViewContext<T>> success;
	private BiConsumer<PaginatedViewContext<T>, Exception> error;


	/**
	 * The handler that will run when the paging data starts loading.
	 *
	 * @param handler The load event handler.
	 */
	public void onLoad(@NotNull Consumer<PaginatedViewContext<T>> handler) {
		this.loadStarted = handler;
	}

	/**
	 * Defines the handler that will be called when the data is successfully loaded.
	 *
	 * @param handler The success event handler.
	 */
	public void onSuccess(@NotNull Consumer<PaginatedViewContext<T>> handler) {
		this.success = handler;
	}

	/**
	 * Defines the handler that will be called when there is an error while loading data.
	 *
	 * @param handler The error event handler.
	 */
	public void onError(@NotNull BiConsumer<PaginatedViewContext<T>, Exception> handler) {
		this.error = handler;
	}

	/**
	 * Defines the handler that will be called when all events complete regardless of whether an error occurs or not.
	 *
	 * @param handler The done event handler.
	 */
	public void onDone(@NotNull Consumer<PaginatedViewContext<T>> handler) {
		this.loadFinished = handler;
	}

}
