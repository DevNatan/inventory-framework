package me.devnatan.inventoryframework.state;

import java.util.function.Supplier;
import lombok.Data;

@Data
public final class ComputedValue implements InternalStateValue {

    private final Supplier<Object> factory;

    @Override
    public Object get() {
        return factory.get();
    }

    @Override
    public void set(Object value) {
        throw new IllegalStateModificationException();
    }
}
