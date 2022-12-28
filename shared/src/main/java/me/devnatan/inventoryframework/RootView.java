package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.state.MutableState;
import me.devnatan.inventoryframework.state.PaginationState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateHolder;
import me.saiintbrisson.minecraft.ViewItem;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface RootView extends VirtualView {

	/**
	 * Creates an immutable computed state.
	 * <p>
	 * A computed state is a state that every time an attempt is made to obtain the value of that
	 * state, the obtained value is computed again by the {@code computation} function.
	 * <pre>{@code
	 * State<Integer> intState = computedState($ -> ThreadLocalRandom::nextInt);
	 *
	 * intState.get(...); // some random number
	 * intState.get(...); // another random number
	 * }</pre>
	 * <p>
	 * This API is experimental and is not subject to the general compatibility guarantees such API
	 * may be changed or may be removed completely in any further release.
	 *
	 * @param computation The function to compute the value.
	 * @param <T>         The state holder type.
	 * @param <R>         The state value type.
	 * @return An immutable computed state.
	 */
	@ApiStatus.Experimental
	<T extends StateHolder, R> State<R> computedState(@NotNull Function<T, R> computation);

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
	 * <p>
	 * This API is experimental and is not subject to the general compatibility guarantees such API
	 * may be changed or may be removed completely in any further release.
	 *
	 * @param computation The function to compute the value.
	 * @param <T>         The state holder type.
	 * @return An immutable computed state.
	 */
	<T> State<T> computedState(@NotNull Supplier<T> computation);

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
	 * <p>
	 * This API is experimental and is not subject to the general compatibility guarantees such API
	 * may be changed or may be removed completely in any further release.
	 *
	 * @param value The value factory.
	 * @param <T>   The state holder type.
	 * @param <R>   The state value type.
	 * @return A lazy state.
	 */
	@ApiStatus.Experimental
	<T extends StateHolder, R> State<R> lazyState(@NotNull Function<T, R> value);

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
	 * <p>
	 * This API is experimental and is not subject to the general compatibility guarantees such API
	 * may be changed or may be removed completely in any further release.
	 *
	 * @param value The value factory.
	 * @param <T>   The state holder type.
	 * @return A lazy state.
	 */
	@ApiStatus.Experimental
	<T> State<T> lazyState(@NotNull Supplier<T> value);

	/**
	 * Creates an immutable {@link #lazyState(Function) lazy state} whose value is always computed
	 * from the initial data set by its {@link StateHolder}.
	 * <p>
	 * When the holder is a {@code OpenViewContext}, the initial value will be the value defined
	 * in the initial opening data of the container. This state is specifically set for backwards
	 * compatibility with the old way of applying data to a context before or during container open.
	 * <p>
	 * As to open a view it is necessary to pass a {@link java.util.Map}, the {@code key} is used to
	 * get the value from that map.
	 * <p>
	 * This API is experimental and is not subject to the general compatibility guarantees such API
	 * may be changed or may be removed completely in any further release.
	 *
	 * @param key The initial data identifier.
	 * @param <T> The initial data value type.
	 * @return A state computed with an initial opening data value.
	 */
	@ApiStatus.Experimental
	<T> State<T> initialState(@NotNull String key);

	/**
	 * Creates an immutable {@link #lazyState(Function) lazy state} whose value is always computed
	 * from the initial data set by its {@link StateHolder}.
	 * <p>
	 * When the holder is a {@code OpenViewContext}, the initial value will be the value defined
	 * in the initial opening data of the container. This state is specifically set for backwards
	 * compatibility with the old way of applying data to a context before or during container open.
	 * <p>
	 * The class parameter is used to convert all initial state into a value. Note that support for
	 * obtaining a specific value from the initial data is only available from version 2.5.4 of the
	 * library.
	 * <p>
	 * This API is experimental and is not subject to the general compatibility guarantees such API
	 * may be changed or may be removed completely in any further release.
	 *
	 * @param stateClassType The initial data class type.
	 * @param <T>            The initial data type.
	 * @return A state computed with an initial opening data value.
	 */
	@ApiStatus.Experimental
	<T> State<T> initialState(@NotNull Class<? extends T> stateClassType);

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
	 * <p>
	 * This API is experimental and is not subject to the general compatibility guarantees such API
	 * may be changed or may be removed completely in any further release.
	 *
	 * @param initialValue The initial value of the state.
	 * @param <T>          The state value type.
	 * @return A mutable state with an initial value.
	 */
	@ApiStatus.Experimental
	<T> MutableState<T> mutableState(T initialValue);

	/**
	 * This API is experimental and is not subject to the general compatibility guarantees such API
	 * may be changed or may be removed completely in any further release.
	 *
	 * @param sourceProvider The data provider for pagination.
	 * @param itemHandler    The function for handling pagination items, this function is called for
	 *                       each paged element (item) on a page.
	 * @param <T>            The state holder type.
	 * @param <V>            The pagination data type.
	 * @return A pagination state.
	 */
//	@ApiStatus.Experimental
//	<T extends StateHolder, V> PaginationState<V> paginationState(
//		@NotNull Function<T, @NotNull List<V>> sourceProvider,
//		@NotNull BiConsumer<ViewItem, V> itemHandler
//	);

}
