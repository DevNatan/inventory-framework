package me.devnatan.inventoryframework.state;

import lombok.Data;

@Data
public final class ImmutableValue implements InternalStateValue {

    private final Object value;

    @Override
    public Object get() {
        return value;
    }

    @Override
    public void set(Object value) {
        cannotBeMutated();
    }

    static void cannotBeMutated() {
        throw new IllegalStateModificationException();
    }
}
