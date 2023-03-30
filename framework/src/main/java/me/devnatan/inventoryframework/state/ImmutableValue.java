package me.devnatan.inventoryframework.state;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class ImmutableValue extends StateValue {

    private final Object value;

    public ImmutableValue(@NotNull State<?> state, Object value) {
        super(state);
        this.value = value;
    }

    @Override
    public Object get() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ImmutableValue that = (ImmutableValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public String toString() {
        return "ImmutableValue{" + "value=" + value + "} " + super.toString();
    }
}
