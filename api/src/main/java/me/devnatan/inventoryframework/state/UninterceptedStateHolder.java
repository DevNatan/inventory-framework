package me.devnatan.inventoryframework.state;

import me.devnatan.inventoryframework.state.internal.DefaultStateHolder;
import org.jetbrains.annotations.NotNull;

abstract class UninterceptedStateHolder extends DefaultStateHolder {

    @Override
    public abstract void updateCaught(@NotNull State<?> state, Object oldValue, Object newValue);
}
