package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap-backed Default implementation for StateHost.
 */
public final class DefaultStateHost implements StateHost {

	private final Map<Long, StateValue> valuesMap = new HashMap<>();

	@Override
	public StateValue getState(long id) {
		return valuesMap.get(id);
	}

	@Override
	public void setState(long id, @NotNull StateValue value) {
		valuesMap.put(id, value);
	}
}
