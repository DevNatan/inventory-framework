package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

/**
 * State whose value is an {@code int} primitive type.
 */
public interface IntState extends State<Integer> {

    /**
     * Gets the current value of this state.
     *
     * @param holder The state holder.
     * @return The current state value.
     */
    int getInt(@NotNull StateHost holder);
}
