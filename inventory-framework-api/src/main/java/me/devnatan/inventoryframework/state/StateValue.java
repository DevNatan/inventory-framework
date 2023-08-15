package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Wrapper of the value of a {@link State} for a single {@link StateValueHost}.
 */
// TODO Must be a interface to allow spread across "component as state" (like Pagination)
public abstract class StateValue {

    private final State<?> state;

    public StateValue(State<?> state) {
        this.state = state;
    }

    /**
     * The state who holds this value.
     *
     * @return The state who holds this value.
     */
    public final State<?> getState() {
        return state;
    }

    /**
     * The id of this state on its current host.
     *
     * @return The state id.
     */
    public final long getId() {
        return getState().internalId();
    }

    /**
     * The current state value.
     * <p>
     * The value returned and consistency with values returned by the same earlier is unknown as
     * this is implementation defined.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @return The current state value.
     */
    @ApiStatus.Internal
    @UnknownNullability
    protected abstract Object get();

    /**
     * Sets the new state value.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param value The new value.
     * @throws StateException If this value can't be set.
     */
    @ApiStatus.Internal
    protected void set(Object value) {
        throw new IllegalStateModificationException("Immutable");
    }
}
