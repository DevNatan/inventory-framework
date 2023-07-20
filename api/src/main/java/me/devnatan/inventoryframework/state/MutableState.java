package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

/**
 * Mutable variation of a {@link State} that the value can be changed.
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
