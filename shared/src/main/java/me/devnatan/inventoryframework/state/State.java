package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiFunction;
import java.util.function.Function;

@ApiStatus.Experimental
public interface State<T> {

	T get();

	void update(StateOwner target, T newValue);

	@SuppressWarnings("unchecked")
	static <T> State<T> of(T initialValue) {
		return (State<T>) new InternalStateHolder(initialValue, false);
	}

	@SuppressWarnings("unchecked")
	static <T, A> State<T> computed(Function<A, T> factory) {
		return (State<T>) new InternalStateHolder(factory, true);
	}

	@SuppressWarnings("unchecked")
	static <T, A, B> State<T> computed(BiFunction<A, B, T> factory) {
		return (State<T>) new InternalStateHolder(factory, true);
	}

}
