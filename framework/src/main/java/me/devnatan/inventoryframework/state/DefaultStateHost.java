package me.devnatan.inventoryframework.state;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class DefaultStateHost implements StateHost {

    private final Map<Long, InternalStateValue> valuesMap = new HashMap<>();

    Object get(long id) {
        if (!valuesMap.containsKey(id)) throw new UninitializedStateException();
        return valuesMap.get(id).get();
    }

    void set(long id, Object newValue) {
        if (!valuesMap.containsKey(id)) throw new UninitializedStateException();
        valuesMap.get(id).set(newValue);
    }

    void init(long id, InternalStateValue value) {
        valuesMap.put(id, value);
    }
}
