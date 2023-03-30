package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

/**
 * Mutable variation of a state that exposes a function to set a new value for a state.
 *
 * @param <T> The state value type.
 */
public interface MutableState<T> extends State<T> {

    /**
     * Sets a new value for this state.
     *
     * @param value The new state value.
     * @param host  The state host that'll catch the state change.
     */
    void set(T value, @NotNull StateValueHost host);
}
