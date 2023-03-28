package me.devnatan.inventoryframework.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * HashMap-backed Default implementation for StateHost.
 */
public final class DefaultStateValueHost implements StateValueHost {

    private final Map<Long, StateValue> valuesMap = new HashMap<>();
    private final List<StateManagementListener> listeners = new ArrayList<>();

    @Override
    public StateValue getState(State<?> state) {
        final long id = state.internalId();
        if (!valuesMap.containsKey(id)) {
            final StateValue value = state.factory().create(this, state);
            initState(id, value, null);
        }

        return valuesMap.get(id);
    }

    @Override
    public void initState(long id, @NotNull StateValue value, Object initialValue) {
        valuesMap.put(id, value);
        for (final StateManagementListener listener : listeners)
            listener.stateValueInitialized(this, value, initialValue);
    }

    @Override
    public void updateState(long id, Object value) {
        final StateValue stateValue = valuesMap.get(id);
        final Object currValue = stateValue.get();
        stateValue.set(value);
        for (final StateManagementListener listener : listeners) {
            listener.stateValueSet(this, stateValue, currValue, value);
        }
    }

    @Override
    public void attachStateListener(@NotNull StateManagementListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void detachStateListener(@NotNull StateManagementListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
}
