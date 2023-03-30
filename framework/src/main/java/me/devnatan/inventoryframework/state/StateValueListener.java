package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface StateValueListener {

    void valueGet(@NotNull State<?> state, @NotNull StateValueHost host, @NotNull StateValue value);

    void valueSet(@NotNull StateValueHost host, @NotNull StateValue value);
}
