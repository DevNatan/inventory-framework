package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * StateHost is an entity capable of storing current data from multiple states.
 */
public interface StateValueHost {

    /**
     * Returns the internal value of a state.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param id The state id.
     * @return A StateValue from the state with the specified id.
     * @throws UninitializedStateException
     */
    @ApiStatus.Internal
    StateValue getState(long id);

    /**
     * Sets the internal value of a state.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param id    The state id.
     * @param value The new state value.
     */
    @ApiStatus.Internal
    void setState(long id, @NotNull StateValue value);
}
