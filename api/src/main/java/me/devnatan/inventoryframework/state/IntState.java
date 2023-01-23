package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface IntState extends State<Integer> {

    /**
     * Gets the current value for this state defined in the specified holder.
     *
     * @param holder The state holder.
     * @return The current state value.
     */
    int getAsInt(@NotNull StateHolder holder);
}
