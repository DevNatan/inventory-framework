package me.devnatan.inventoryframework.state;

import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class ImmutableValue extends AbstractStateValue {

    private final Object value;

    public ImmutableValue(long internalId, Object value) {
        super(internalId);
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
