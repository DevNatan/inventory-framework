package me.devnatan.inventoryframework.internal.state;

import lombok.Getter;
import me.devnatan.inventoryframework.state.StateMarker;
import me.devnatan.inventoryframework.state.StateHolder;
import me.devnatan.inventoryframework.state.StateValueHolder;
import org.jetbrains.annotations.NotNull;

final class InternalMutableStateValueHolder implements StateValueHolder {

    @Getter
    private final StateMarker<?> state;

    private final StateHolder origin;
    private Object currValue;

    InternalMutableStateValueHolder(@NotNull StateMarker<?> state, @NotNull StateHolder origin, Object initialValue) {
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
