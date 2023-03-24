package me.devnatan.inventoryframework.state;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StateValueFactory {

	/**
	 * Creates a new immutable value.
	 *
	 * @param id           The inherited state id.
	 * @param initialValue The initial value of the state.
	 * @return A new immutable {@link StateValue} instance.
	 */
	public static StateValue createImmutable(long id, Object initialValue) {
		return new ImmutableValue(id, initialValue);
	}

	/**
	 * Creates a new mutable value.
	 *
	 * @param id           The inherited state id.
	 * @param initialValue The initial value of the state.
	 * @return A new mutable {@link StateValue} instance.
	 */
	public static StateValue createMutable(long id, Object initialValue) {
		return new MutableValue(id, initialValue);
	}

	/**
	 * Creates a new computed value.
	 *
	 * @param id      The inherited state id.
	 * @param factory The values factory for the state.
	 * @return A new computed {@link StateValue} instance.
	 */
	public static StateValue createComputed(long id, @NotNull Supplier<Object> factory) {
		return new ComputedValue(id, factory);
	}

	/**
	 * Creates a new lazy value.
	 *
	 * @param id          The inherited state id.
	 * @param computation The initial computation of this value.
	 * @return A new lazy {@link StateValue} instance.
	 */
	public static StateValue createLazy(long id, @NotNull Supplier<Object> computation) {
		return new LazyValue(id, computation);
	}
}
