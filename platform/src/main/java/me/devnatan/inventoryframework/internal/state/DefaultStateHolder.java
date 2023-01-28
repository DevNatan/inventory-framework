package me.devnatan.inventoryframework.internal.state;

import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateHolder;
import me.devnatan.inventoryframework.state.StateValueHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

// TODO allow observer registration
@ApiStatus.Internal
public class DefaultStateHolder implements StateHolder {

	private static final AtomicLong ids = new AtomicLong(0);
	private final Map<Long, StateValueHolder> statesMap = new HashMap<>();

	@Override
	public long generateId() {
		return nextId();
	}

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
	public StateValueHolder createUnchecked(Object initialValue) {
		final long id = generateId();
		final StateValueHolder impl = new InternalMutableStateValueHolder(new MutableState<>(id), this, initialValue);
		synchronized (statesMap) {
			if (statesMap.containsKey(id)) throw new IllegalStateException("State conflict: " + id);

			statesMap.put(id, impl);
		}
		return impl;
	}

	@Override
	public <T> void watch(@NotNull State<?> state, @NotNull BiConsumer<T, T> callback) {
		throw new UnsupportedOperationException("Watching states is not yet supported");
	}

	static synchronized long nextId() {
		return ids.getAndIncrement();
	}
}
