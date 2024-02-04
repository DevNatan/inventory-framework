package me.devnatan.inventoryframework.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.devnatan.inventoryframework.IFDebug;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * HashMap-backed state container to store a collection of states.
 *
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class StateRegistry implements Iterable<State<?>> {

    private final Map<Long, State<?>> stateMap = new HashMap<>();

    /**
     * Returns a state from the states map that have the specified id.
     *
     * @param id The id of the state.
     * @return A StatePhase of the specified id.
     */
    public State<?> getState(long id) {
        return stateMap.get(id);
    }

    /**
     * Adds a new state to the states' collection.
     *
     * @param state The state to be added.
     */
    public void registerState(@NotNull State<?> state, Object caller) {
        synchronized (stateMap) {
            stateMap.put(state.internalId(), state);
            IFDebug.debug(
                    "StatePhase %s (id: %d) registered in %s",
                    state.getClass().getName(),
                    state.internalId(),
                    caller.getClass().getName());
        }
    }

    /**
     * Removes a state from the states' collection.
     *
     * @param stateId The id of the state to be removed.
     */
    public void unregisterState(long stateId, Object caller) {
        synchronized (stateMap) {
            final State<?> state = stateMap.remove(stateId);
            IFDebug.debug(
                    "StatePhase %s unregistered from %s",
                    state.internalId(), caller.getClass().getName());
        }
    }

    @NotNull
    @Override
    public Iterator<State<?>> iterator() {
        return Collections.unmodifiableCollection(stateMap.values()).iterator();
    }
}
