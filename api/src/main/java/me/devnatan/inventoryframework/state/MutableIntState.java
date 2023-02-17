package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface MutableIntState extends IntState, MutableState<Integer> {

    /**
     * Sets a new value for this state.
     *
     * @param value  The new state value.
     * @param holder The state holder that'll get this update.
     */
    void setInt(int value, @NotNull StateHost holder);

    /**
     * Increments the current value of this state by {@code 1}.
     *
     * @param holder The state holder that'll get this update.
     */
    void increment(@NotNull StateHost holder);

    /**
     * Increments the current value of this state by {@code 1}.
     *
     * @param holder The state holder that'll get this update.
     */
    void decrement(@NotNull StateHost holder);
}
