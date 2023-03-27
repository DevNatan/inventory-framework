package me.devnatan.inventoryframework.state;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * HashMap-backed Default implementation for StateHost.
 */
public final class DefaultStateHost implements StateValueHost {

    private final Map<Long, StateValue> valuesMap = new HashMap<>();

    @Override
    public StateValue getState(long id) {
        if (!valuesMap.containsKey(id)) throw new UninitializedStateException();

        return valuesMap.get(id);
    }

    @Override
    public void setState(long id, @NotNull StateValue value) {
        valuesMap.put(id, value);
    }
}
