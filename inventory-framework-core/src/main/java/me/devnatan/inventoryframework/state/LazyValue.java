package me.devnatan.inventoryframework.state;

import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Lazy value whose initial value is undefined if the host has not previously attempted to retrieve it.
 * <p>
 * The initial state value is set by a {@link LazyValue#computation}, and this value remains the
 * {@link LazyValue#currValue current value} throughout the lifecycle of that value.
 *
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class LazyValue extends StateValue {

    private static final Object UNINITIALIZED = new Object();

    private final Supplier<?> computation;
    private Object currValue = UNINITIALIZED;

    public LazyValue(@NotNull State<?> state, @NotNull Supplier<?> computation) {
        super(state);
        this.computation = computation;
    }

    @Override
    public Object get() {
        if (currValue.equals(UNINITIALIZED)) currValue = computation.get();

        return currValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LazyValue lazyValue = (LazyValue) o;
        return computation.equals(lazyValue.computation) && Objects.equals(currValue, lazyValue.currValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), computation, currValue);
    }

    @Override
    public String toString() {
        return "LazyValue{" + "computation=" + computation + ", currValue=" + currValue + "} " + super.toString();
    }
}
