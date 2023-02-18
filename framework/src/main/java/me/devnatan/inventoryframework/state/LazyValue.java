package me.devnatan.inventoryframework.state;

import static me.devnatan.inventoryframework.state.ImmutableValue.cannotBeMutated;

import java.util.function.Supplier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public final class LazyValue implements InternalStateValue {

    public static final Object uninitialized = new Object();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Supplier<Object> computation;

    private Object currValue = uninitialized;

    @Override
    public Object get() {
        if (currValue.equals(uninitialized)) currValue = computation.get();

        return currValue;
    }

    @Override
    public void set(Object value) {
        cannotBeMutated();
    }
}
