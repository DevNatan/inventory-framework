package me.devnatan.inventoryframework.state;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public final class StateValueFactory {

    /**
     * Creates a new immutable value.
     *
     * @param id           The inherited state id.
     * @param initialValue The initial value of the state.
     * @return A new immutable {@link StateValue} instance.
     */
    public StateValue createImmutable(long id, Object initialValue) {
        return new ImmutableValue(id, initialValue);
    }

    /**
     * Creates a new mutable value.
     *
     * @param id           The inherited state id.
     * @param initialValue The initial value of the state.
     * @return A new mutable {@link StateValue} instance.
     */
    public StateValue createMutable(long id, Object initialValue) {
        return new MutableValue(id, initialValue);
    }

    /**
     * Creates a new computed value.
     *
     * @param id      The inherited state id.
     * @param factory The values factory for the state.
     * @return A new computed {@link StateValue} instance.
     */
    public StateValue createComputed(long id, @NotNull Supplier<Object> factory) {
        return new ComputedValue(id, factory);
    }

    /**
     * Creates a new lazy value.
     *
     * @param id          The inherited state id.
     * @param computation The initial computation of this value.
     * @return A new lazy {@link StateValue} instance.
     */
    public StateValue createLazy(long id, @NotNull Supplier<Object> computation) {
        return new LazyValue(id, computation);
    }
}
