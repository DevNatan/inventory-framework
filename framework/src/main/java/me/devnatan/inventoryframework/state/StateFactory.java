package me.devnatan.inventoryframework.state;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class StateFactory {

    private static final AtomicLong ids = new AtomicLong();

    public <T> State<T> createState(@NotNull Function<StateHost, InternalStateValue> valueFactory) {
        return new StateImpl<>(nextStateId(), valueFactory);
    }

    private static synchronized long nextStateId() {
        return ids.getAndIncrement();
    }
}
