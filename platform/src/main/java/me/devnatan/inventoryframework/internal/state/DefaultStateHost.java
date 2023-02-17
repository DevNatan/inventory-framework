package me.devnatan.inventoryframework.internal.state;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateHandler;
import me.devnatan.inventoryframework.state.StateHost;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

// TODO allow observer registration
@ApiStatus.Internal
public class DefaultStateHost implements StateHost {

    private final Map<Long, StateValueHolder> statesMap = new HashMap<>();

    @Override
    public StateValueHolder retrieve(long id) {
        return statesMap.get(id);
    }

    @Override
    public void updateCaught(@NotNull State<?> state, Object oldValue, Object newValue) {
        // initial value is always the first
        //		newState(state, newValue);

        if (!(state instanceof MutableState)) throw new IllegalStateException("Immutable state");

        // TODO retrieve
        //        retrieve(state.getId()).set(newValue);
        // TODO intercept
    }

    @Override
    public StateValueHolder createMutable(Object initialValue) {
        final long id = State.generateId();
        return register(id, new InternalMutableStateValueHolder(new MutableState<>(id), this, initialValue));
    }

    @Override
    public StateValueHolder createUnchecked(long id, State<?> state, Object initialValue) {
        return register(id, new InternalMutableStateValueHolder(state, this, initialValue));
    }

    private StateValueHolder register(long id, StateValueHolder value) {
        synchronized (statesMap) {
            if (statesMap.containsKey(id)) throw new IllegalStateException(String.format("State conflict %s", id));

            statesMap.put(id, value);
        }

        if (value.getState() instanceof StateHandler) ((StateHandler) value.getState()).attached(id, this);
        return value;
    }

    @Override
    public <T> void watch(@NotNull State<?> state, @NotNull BiConsumer<T, T> callback) {
        throw new UnsupportedOperationException("Watching states is not yet supported");
    }
}
