package me.devnatan.inventoryframework.state;

import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Computed value whose value returned by the function to get the state value is always a new value
 * created by {@link ComputedValue#factory}.
 *
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class ComputedValue extends StateValue {

    private final Supplier<?> factory;

    public ComputedValue(@NotNull State<?> state, @NotNull Supplier<?> factory) {
        super(state);
        this.factory = factory;
    }

    @Override
    public Object get() {
        return factory.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ComputedValue that = (ComputedValue) o;
        return factory.equals(that.factory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), factory);
    }

    @Override
    public String toString() {
        return "ComputedValue{" + "computation=" + factory + "} " + super.toString();
    }
}
