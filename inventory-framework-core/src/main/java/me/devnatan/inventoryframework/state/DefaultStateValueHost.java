package me.devnatan.inventoryframework.state;

import java.util.HashMap;
import java.util.Map;
import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.jetbrains.annotations.ApiStatus;

/**
 * HashMap-backed Default implementation for StateHost.
 * <p>
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public class DefaultStateValueHost implements StateValueHost {

    private final Map<Long, StateValue> valuesMap = new HashMap<>();
    private final Pipeline<StateValue> pipeline = new Pipeline<>(PipelinePhase.State.values());

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
        getPipeline().execute(PipelinePhase.StateValue.STATE_VALUE_GET, value);
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
    public final void initializeState(long id, StateValue value) {
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

        getPipeline().execute(PipelinePhase.StateValue.STATE_VALUE_SET, stateValue);
    }

    @Override
    public final void watchState(long id, StateWatcher watcher) {
        interceptPipelineCall(PipelinePhase.StateValue.STATE_VALUE_SET, watcher);
    }

    Pipeline<StateValue> getPipeline() {
        return pipeline;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public final void interceptPipelineCall(PipelinePhase phase, PipelineInterceptor<?> interceptor) {
        getPipeline().intercept(phase, (PipelineInterceptor) interceptor);
    }
}
