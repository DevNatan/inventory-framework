package me.saiintbrisson.minecraft.state;

import me.saiintbrisson.minecraft.state.internal.DefaultStateOwner;
import org.jetbrains.annotations.NotNull;

abstract class UninterceptedStateOwner extends DefaultStateOwner {

    @Override
    public abstract void updateCaught(@NotNull State<?> state, Object oldValue, Object newValue);
}
