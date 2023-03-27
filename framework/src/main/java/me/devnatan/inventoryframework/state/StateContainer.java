package me.devnatan.inventoryframework.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * HashMap-backed state container to store a collection of states.
 */
public final class StateContainer implements Iterable<State<?>> {

    private final Map<Long, State<?>> stateMap = new HashMap<>();

    @Getter
    private final StateValueFactory valueFactory = new StateValueFactory();

    /**
     * Adds a new state to the states' collection.
     *
     * @param state The state to be added.
     */
    public void addState(@NotNull State<?> state) {
        synchronized (stateMap) {
            stateMap.put(state.internalId(), state);
        }
    }

    /**
     * Removes a state from the states' collection.
     *
     * @param stateId The id of the state to be removed.
     */
    public void removeState(long stateId) {
        synchronized (stateMap) {
            stateMap.remove(stateId);
        }
    }

    @NotNull
    @Override
    public Iterator<State<?>> iterator() {
        return Collections.unmodifiableCollection(stateMap.values()).iterator();
    }
}
