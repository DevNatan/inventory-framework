package me.devnatan.inventoryframework.state;

import java.util.HashMap;
import java.util.Map;

public final class DefaultStateHost implements StateHost {

    private final Map<Long, InternalStateValue> valuesMap = new HashMap<>();

    synchronized Object get(long state, State<?> instance, InternalStateValue value) {
        if (!valuesMap.containsKey(state)) valuesMap.put(state, value);

        return valuesMap.get(state).get();
    }

    synchronized void set(long state, InternalStateValue stateValue, Object newValue) {
        valuesMap.computeIfAbsent(state, $ -> stateValue).set(newValue);
    }
}
