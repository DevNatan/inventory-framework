package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StateUpdateListener<T, V> {

    /**
     * Called when a value of a {@link StateValue} is updated.
     *
     * @param host The host of the value.
     * @param oldValue The previous value.
     * @param newValue The new value.
     */
    void onUpdate(@NotNull T host, V oldValue, V newValue);
}
