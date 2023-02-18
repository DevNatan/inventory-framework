package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface State<T> {

    /**
     * Gets the current value for this state defined in the specified host.
     *
     * @param host The state host.
     * @return The current state value.
     */
    T get(@NotNull StateHost host);
}
