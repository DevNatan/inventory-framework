package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.MutableState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateAccess;
import me.devnatan.inventoryframework.state.StateAccessImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public abstract class PlatformComponent<CONTEXT extends IFContext, BUILDER extends ComponentBuilder<BUILDER>, ITEM_BUILDER extends ItemComponentBuilder<ITEM_BUILDER>>
	extends AbstractComponent
	implements Component, StateAccess<CONTEXT, ITEM_BUILDER> {

	private final StateAccess<CONTEXT, ITEM_BUILDER> stateAccess;

	// User Provided
	private final Consumer<? super IFComponentRenderContext> renderHandler;
	private final Consumer<? super IFComponentUpdateContext> updateHandler;
	private final Consumer<? super IFSlotClickContext> clickHandler;

	{
		final RootView root = (RootView) getRootAsContext().getRoot();
		stateAccess = new StateAccessImpl<>(root.getElementFactory(), ((PlatformView) root).getStateRegistry());
	}

    /**
     * Creates a new ComponentBuilder instance to configure this component.
     *
     * @return A new ComponentBuilder instance.
     */
    public abstract BUILDER createBuilder();

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
	public final <T> State<Pagination> paginationState(@NotNull List<? super T> sourceProvider, @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> elementConsumer) {
		return stateAccess.paginationState(sourceProvider, elementConsumer);
	}

	@Override
	public final <T> State<Pagination> computedPaginationState(@NotNull Function<CONTEXT, List<? super T>> sourceProvider, @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
		return stateAccess.computedPaginationState(sourceProvider, valueConsumer);
	}

	@Override
	public final <T> State<Pagination> computedAsyncPaginationState(@NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider, @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
		return stateAccess.computedAsyncPaginationState(sourceProvider, valueConsumer);
	}

	@Override
	public final <T> State<Pagination> lazyPaginationState(@NotNull Function<CONTEXT, List<? super T>> sourceProvider, @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
		return stateAccess.lazyPaginationState(sourceProvider, valueConsumer);
	}

	@Override
	public final <T> State<Pagination> lazyPaginationState(@NotNull Supplier<List<? super T>> sourceProvider, @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
		return stateAccess.lazyPaginationState(sourceProvider, valueConsumer);
	}

	@Override
	public final <T> State<Pagination> lazyAsyncPaginationState(@NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider, @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, T> valueConsumer) {
		return stateAccess.lazyAsyncPaginationState(sourceProvider, valueConsumer);
	}

	@Override
	public final <T> PaginationStateBuilder<CONTEXT, ITEM_BUILDER, T> buildPaginationState(@NotNull List<? super T> sourceProvider) {
		return stateAccess.buildPaginationState(sourceProvider);
	}

	@Override
	public final <T> PaginationStateBuilder<CONTEXT, ITEM_BUILDER, T> buildComputedPaginationState(@NotNull Function<CONTEXT, List<? super T>> sourceProvider) {
		return stateAccess.buildComputedPaginationState(sourceProvider);
	}

	@Override
	public final <T> PaginationStateBuilder<CONTEXT, ITEM_BUILDER, T> buildComputedAsyncPaginationState(@NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider) {
		return stateAccess.buildComputedAsyncPaginationState(sourceProvider);
	}

	@Override
	public final <T> PaginationStateBuilder<CONTEXT, ITEM_BUILDER, T> buildLazyPaginationState(@NotNull Supplier<List<? super T>> sourceProvider) {
		return stateAccess.buildLazyPaginationState(sourceProvider);
	}

	@Override
	public final <T> PaginationStateBuilder<CONTEXT, ITEM_BUILDER, T> buildLazyPaginationState(@NotNull Function<CONTEXT, List<? super T>> sourceProvider) {
		return stateAccess.buildLazyPaginationState(sourceProvider);
	}

	@Override
	public final <T> PaginationStateBuilder<CONTEXT, ITEM_BUILDER, T> buildLazyAsyncPaginationState(@NotNull Function<CONTEXT, CompletableFuture<List<T>>> sourceProvider) {
		return stateAccess.buildLazyAsyncPaginationState(sourceProvider);
	}
}
