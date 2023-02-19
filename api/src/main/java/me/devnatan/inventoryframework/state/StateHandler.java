package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface StateHandler {

    void attached(long id, @NotNull StateHost holder);
}
