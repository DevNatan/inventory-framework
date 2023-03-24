package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

public interface State<T> {

	AtomicLong ids = new AtomicLong();

	/**
	 * Gets the current value for this state defined in the specified host.
	 *
	 * @param host The state host.
	 * @return The current state value.
	 */
	T get(@NotNull StateHost host);

	/**
	 * Generates a new state id.
	 *
	 * @return A new unique state id.
	 */
	static long next() {
		return ids.getAndIncrement();
	}

}
