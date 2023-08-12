package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface MutableIntState extends MutableState<Integer> {

    /**
     * Increments the current state value by {@code 1}.
     *
     * @param host The state host that'll catch the state change.
     * @return The updated value.
     */
    int increment(@NotNull StateValueHost host);

    /**
     * Decrements the current state value by {@code 1}.
     *
     * @param host The state host that'll catch the state change.
     * @return The updated value.
     */
    int decrement(@NotNull StateValueHost host);
}
