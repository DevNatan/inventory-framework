package me.devnatan.inventoryframework.state;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * HashMap-backed Default implementation for StateHost.
 */
public class DefaultStateValueHost implements StateValueHost {

    private final Map<Long, StateValue> valuesMap = new HashMap<>();

    @Override
    public Object getState(State<?> state) {
        final long id = state.internalId();
        if (!valuesMap.containsKey(id)) {
            final StateValue value = state.factory().create(this, state);
            initState(id, value, null);
        }

        final StateValue stateValue = valuesMap.get(id);
        final Object result = stateValue.get();
        callListeners(stateValue, listener -> listener.stateValueGet(state, this, stateValue, result));
        return result;
    }

    @Override
    public void initState(long id, @NotNull StateValue value, Object initialValue) {
        valuesMap.put(id, value);
        callListeners(value, listener -> listener.stateValueInitialized(this, value, initialValue));
    }

    @Override
    public void updateState(long id, Object value) {
        final StateValue stateValue = valuesMap.get(id);
        final Object currValue = stateValue.get();
        stateValue.set(value);
        callListeners(stateValue, listener -> listener.stateValueSet(this, stateValue, currValue, value));
    }

    private void callListeners(@NotNull StateValue value, Consumer<StateManagementListener> call) {
        if (value instanceof StateManagementListener) call.accept((StateManagementListener) value);
        if (value.getState() instanceof StateManagementListener)
            call.accept((StateManagementListener) value.getState());
    }
}
