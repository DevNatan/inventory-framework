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
     * @param state The target state.
     * @return A StateValue from the state with the specified id.
     */
    @ApiStatus.Internal
    Object getState(State<?> state);

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
    void initState(long id, @NotNull StateValue value, Object initialValue);

    @ApiStatus.Internal
    void updateState(long id, Object value);
}
