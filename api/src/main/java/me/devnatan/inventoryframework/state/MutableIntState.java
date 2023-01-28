package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface MutableIntState extends IntState, MutableState {

	/**
	 * Sets a new value for this state.
	 *
	 * @param value  The new state value.
	 * @param holder The state holder that'll get this update.
	 */
	void set(int value, @NotNull StateHolder holder);

	/**
	 * Increments the current value of this state by {@code 1}.
	 *
	 * @param holder The state holder that'll get this update.
	 */
	void increment(@NotNull StateHolder holder);

	/**
	 * Increments the current value of this state by {@code 1}.
	 *
	 * @param holder The state holder that'll get this update.
	 */
	void decrement(@NotNull StateHolder holder);
}
