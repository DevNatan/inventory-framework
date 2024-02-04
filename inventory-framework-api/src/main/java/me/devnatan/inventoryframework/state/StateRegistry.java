package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Container to store a collection of states.
 * <p>
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public interface StateRegistry extends Iterable<State<?>> {

    /**
     * Returns a state from the states map that have the specified id.
     *
     * @param id The id of the state.
     * @return A StatePhase of the specified id.
     */
    State<?> getState(long id);

    /**
     * Adds a new state to the states' collection.
     *
     * @param state The state to be added.
     */
    void registerState(@NotNull State<?> state, Object caller);

    /**
     * Removes a state from the states' collection.
     *
     * @param stateId The id of the state to be removed.
     */
    void unregisterState(long stateId, Object caller);
}
