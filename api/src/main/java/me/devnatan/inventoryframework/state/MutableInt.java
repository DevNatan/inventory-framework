package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface MutableInt extends IntState {

    /**
     * Sets a new value for this state in the specified holder.
     *
     * @param value  The new state value.
     * @param holder The state holder that'll get this update.
     */
    void set(int value, @NotNull StateHolder holder);

    void increment(@NotNull StateHolder holder);

    void decrement(@NotNull StateHolder holder);
}
