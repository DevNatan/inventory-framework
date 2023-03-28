package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StateValueFactory {

    /**
     * Creates a new value for the given host and state.
     *
     * @param host  The state host.
     * @param state The state.
     * @return A new {@link StateValue}.
     */
    @NotNull
    StateValue create(@NotNull StateValueHost host, @NotNull State<?> state);
}
