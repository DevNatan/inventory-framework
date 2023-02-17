package me.devnatan.inventoryframework.state;

import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;

public interface State<T> {

    AtomicLong ids = new AtomicLong(0);

    /**
     * Gets the current value for this state defined in the specified holder.
     *
     * @param holder The state holder.
     * @return The current state value.
     */
    T get(@NotNull StateHost holder);

    static long generateId() {
        return ids.getAndIncrement();
    }
}
