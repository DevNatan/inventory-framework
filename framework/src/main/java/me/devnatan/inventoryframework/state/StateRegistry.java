package me.devnatan.inventoryframework.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * HashMap-backed state container to store a collection of states.
 */
public final class StateRegistry implements Iterable<State<?>> {

    private final Map<Long, State<?>> stateMap = new HashMap<>();

    /**
     * Adds a new state to the states' collection.
     *
     * @param state The state to be added.
     */
    public void registerState(@NotNull State<?> state, Object caller) {
        synchronized (stateMap) {
            stateMap.put(state.internalId(), state);
            if (state instanceof StateWatcher) ((StateWatcher) state).stateRegistered(state, caller);
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
            if (state instanceof StateWatcher) ((StateWatcher) state).stateUnregistered(state, caller);
        }
    }

    @NotNull
    @Override
    public Iterator<State<?>> iterator() {
        return Collections.unmodifiableCollection(stateMap.values()).iterator();
    }
}
