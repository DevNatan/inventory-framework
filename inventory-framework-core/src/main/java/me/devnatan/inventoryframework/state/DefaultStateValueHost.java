package me.devnatan.inventoryframework.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * HashMap-backed Default implementation for StateHost.
 * <p>
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public class DefaultStateValueHost implements StateValueHost {

    private final Map<Long, StateValue> valuesMap = new HashMap<>();
    private final Map<Long, List<StateWatcher>> listeners = new HashMap<>();

    @Override
    public Object getState(State<?> state) {
        final long id = state.internalId();
        final StateValue value;
        if (!valuesMap.containsKey(id)) {
            value = state.factory().create(this, state);
            initializeState(id, value);
        } else {
            value = valuesMap.get(id);
        }

        final Object result = value.get();
        callListeners(value, listener -> listener.stateValueGet(state, this, value, result));
        return result;
    }

    @Override
    public void initializeState(long id, @NotNull StateValue value) {
        valuesMap.put(id, value);
    }

    @Override
    public void updateState(long id, Object value) {
        final StateValue stateValue = valuesMap.get(id);
        final Object currValue = stateValue.get();
        stateValue.set(value);
        callListeners(stateValue, listener -> listener.stateValueSet(this, stateValue, currValue, value));
    }

    @Override
    public void watchState(long id, StateWatcher listener) {
        listeners.computeIfAbsent(id, $ -> new ArrayList<>()).add(listener);
    }

    private void callListeners(@NotNull StateValue value, Consumer<StateWatcher> call) {
        if (value instanceof StateWatcher) call.accept((StateWatcher) value);
        if (value.getState() instanceof StateWatcher) call.accept((StateWatcher) value.getState());

        if (!listeners.containsKey(value.getId())) return;

        listeners.get(value.getId()).forEach(call);
    }
}
