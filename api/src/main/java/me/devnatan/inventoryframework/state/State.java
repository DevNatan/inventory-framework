package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface State<T> extends AindaNaoTenhoNomePraIsso {

    /**
     * Gets the current value for this state defined in the specified holder.
     *
     * @param holder The state holder.
     * @return The current state value.
     */
    T get(@NotNull StateHolder holder);
}
