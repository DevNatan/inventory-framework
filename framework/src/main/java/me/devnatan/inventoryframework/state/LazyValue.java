package me.devnatan.inventoryframework.state;

import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

/**
 * Lazy value whose initial value is undefined if the host has not previously attempted to retrieve it.
 * <p>
 * The initial state value is set by a {@link LazyValue#computation}, and this value remains the
 * {@link LazyValue#currValue current value} throughout the lifecycle of that value.
 */
public final class LazyValue extends StateValue {

    private static final Object UNINITIALIZED = new Object();

    private final Supplier<Object> computation;
    private Object currValue = UNINITIALIZED;

    LazyValue(@NotNull State<?> state, @NotNull Supplier<Object> computation) {
        super(state);
        this.computation = computation;
    }

    @Override
    public Object get() {
        if (currValue.equals(UNINITIALIZED)) currValue = computation.get();

        return currValue;
    }

    @Override
    public void set(Object value) {
        throw new IllegalStateModificationException("Immutable");
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
