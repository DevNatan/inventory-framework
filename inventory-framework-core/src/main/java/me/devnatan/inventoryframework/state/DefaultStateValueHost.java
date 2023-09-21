package me.devnatan.inventoryframework.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import me.devnatan.inventoryframework.IFDebug;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

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
    public @UnmodifiableView Map<Long, StateValue> getStateValues() {
        return Collections.unmodifiableMap(valuesMap);
    }

    @Override
    public StateValue getUninitializedStateValue(long stateId) {
        return valuesMap.get(stateId);
    }

    @Override
    public Object getRawStateValue(State<?> state) {
        final StateValue value = getInternalStateValue(state);
        final Object result = value.get();
        callListeners(value, listener -> listener.stateValueGet(state, this, value, result));
        return result;
    }

    @Override
    public StateValue getInternalStateValue(State<?> state) {
        final long id = state.internalId();
        StateValue value = getUninitializedStateValue(id);
        if (value == null) {
            value = state.factory().create(this, state);
            initializeState(id, value);
            IFDebug.debug("State %s initialized (initialValue = %s)", id, value.toString());
        }

        return value;
    }

    @Override
    public void initializeState(long id, @NotNull StateValue value) {
        valuesMap.put(id, value);
    }

    @Override
    public void updateState(long id, Object value) {
        final StateValue stateValue = getUninitializedStateValue(id);
        final Object oldValue = stateValue.get();
        stateValue.set(value);

        final Object newValue = stateValue.get();
        IFDebug.debug("State %s updated (oldValue = %s, newValue = %s)", id, oldValue, newValue);
        callListeners(stateValue, listener -> listener.stateValueSet(this, stateValue, oldValue, newValue));
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
