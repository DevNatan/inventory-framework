package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface StateManagementListener {

    /**
     * Called when a state is registered on a host.
     *
     * @param state  The registered state.
     * @param caller Who registered the state.
     */
    void stateRegistered(@NotNull State<?> state, Object caller);

    void stateUnregistered(@NotNull State<?> state);

    void stateValueInitialized(@NotNull StateValueHost host, @NotNull StateValue value, Object initialValue);

    /**
     * Called when the value of a state on a specific host is obtained.
     *
     * @param state         The state.
     * @param host          The host.
     * @param internalValue The retrieved value as an internal value.
     * @param rawValue      The raw value from the internal value.
     */
    void stateValueGet(
            @NotNull State<?> state, @NotNull StateValueHost host, @NotNull StateValue internalValue, Object rawValue);

    /**
     * Called when the value of a state on a host is set.
     *
     * @param host        The host.
     * @param value       The retrieved value as an internal value.
     * @param rawOldValue The previous state value.
     * @param rawNewValue The new state value.
     */
    void stateValueSet(@NotNull StateValueHost host, @NotNull StateValue value, Object rawOldValue, Object rawNewValue);
}
