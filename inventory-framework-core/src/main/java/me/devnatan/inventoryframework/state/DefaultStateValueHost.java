package me.devnatan.inventoryframework.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import me.devnatan.inventoryframework.IFDebug;
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
    private final Map<Long, List<StateWatcher>> watchers = new HashMap<>();

    @ApiStatus.Internal
    public Map<Long, List<StateWatcher>> getStateWatchers() {
        return watchers;
    }

    @Override
    public Map<Long, StateValue> getStateValues() {
        return valuesMap;
    }

    @Override
    public final StateValue getUninitializedStateValue(long stateId) {
        final StateValue value = getStateValues().get(stateId);
        if (value == null) {
            IFDebug.debug("State %s not found in %s", stateId, getStateValues());
        }
        return value;
    }

    @Override
    public final Object getRawStateValue(State<?> state) {
        final StateValue value = getInternalStateValue(state);
        final Object result = value.get();
        callStateListeners(value, listener -> listener.stateValueGet(state, this, value, result));
        return result;
    }

    @Override
    public final StateValue getInternalStateValue(State<?> state) {
        final long id = state.internalId();
        StateValue value = getUninitializedStateValue(id);
        if (value == null) {
            value = state.factory().create(this, state);
            initializeState(id, value);
        }

        return value;
    }

    @Override
    public final void initializeState(long id, @NotNull StateValue value) {
        getStateValues().put(id, value);
        IFDebug.debug(
                "State value initialized in %s (id = %s, initialValue = %s)",
                getClass().getName(), id, value.toString());
    }

    @Override
    public final void updateState(long id, Object value) {
        final StateValue stateValue = getUninitializedStateValue(id);
        final Object oldValue = stateValue.get();
        stateValue.set(value);

        final Object newValue = stateValue.get();
        IFDebug.debug(
                "State value updated in %s (id = %s, oldValue = %s, newValue = %s)",
                getClass().getName(), id, oldValue, newValue);
        callStateListeners(stateValue, listener -> listener.stateValueSet(this, stateValue, oldValue, newValue));
    }

    @Override
    public final void watchState(long id, StateWatcher listener) {
        getStateWatchers().computeIfAbsent(id, $ -> new ArrayList<>()).add(listener);
    }

    protected void callStateListeners(@NotNull StateValue value, Consumer<StateWatcher> call) {
        if (value instanceof StateWatcher) call.accept((StateWatcher) value);

        if (getStateWatchers().containsKey(value.internalId()))
            getStateWatchers().get(value.internalId()).forEach(call);
    }
}
