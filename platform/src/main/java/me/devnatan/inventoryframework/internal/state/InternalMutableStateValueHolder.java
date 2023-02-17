package me.devnatan.inventoryframework.internal.state;

import lombok.Getter;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateHost;
import org.jetbrains.annotations.NotNull;

final class InternalMutableStateValueHolder implements StateValueHolder {

    @Getter
    private final State<?> state;

    private final StateHost origin;
    private Object currValue;

    InternalMutableStateValueHolder(@NotNull State<?> state, @NotNull StateHost origin, Object initialValue) {
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
