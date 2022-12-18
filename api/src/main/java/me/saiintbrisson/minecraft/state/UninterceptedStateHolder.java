package me.saiintbrisson.minecraft.state;

import me.saiintbrisson.minecraft.state.internal.DefaultStateHolder;
import org.jetbrains.annotations.NotNull;

abstract class UninterceptedStateHolder extends DefaultStateHolder {

    @Override
    public abstract void updateCaught(@NotNull State<?> state, Object oldValue, Object newValue);
}
