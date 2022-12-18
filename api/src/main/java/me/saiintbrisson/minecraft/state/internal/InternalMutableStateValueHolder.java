package me.saiintbrisson.minecraft.state.internal;

import lombok.Getter;
import me.saiintbrisson.minecraft.state.State;
import me.saiintbrisson.minecraft.state.StateHolder;
import me.saiintbrisson.minecraft.state.StateValueHolder;
import org.jetbrains.annotations.NotNull;

final class InternalMutableStateValueHolder implements StateValueHolder {

    @Getter
    private final State<?> state;

    private final StateHolder origin;
    private Object currValue;

    InternalMutableStateValueHolder(@NotNull State<?> state, @NotNull StateHolder origin, Object initialValue) {
        this.state = state;
        this.origin = origin;
        this.currValue = initialValue;
    }

    @Override
    public Object get() {
        // TODO atomic get
        return currValue;
    }

    @Override
    public void set(Object newValue) {
        // TODO atomic set
        this.currValue = newValue;
    }
}
