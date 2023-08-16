package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Wrapper of the value of a {@link State} for a single {@link StateValueHost}.
 * <p>
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public interface StateValue {

    /**
     * The id of this state on its current host.
     *
     * @return The state id.
     */
    long getId();

    /**
     * The state who holds this value.
     *
     * @return The state who holds this value.
     */
    State<?> getState();

    /**
     * The current state value.
     * <p>
     * The value returned and consistency with values returned by the same earlier is unknown as
     * this is implementation defined.
     *
     * @return The current state value.
     */
    @UnknownNullability
    Object get();

    /**
     * Sets the new state value.
     *
     * @param value The new value.
     * @throws StateException If this value can't be set.
     */
    void set(Object value);
}
