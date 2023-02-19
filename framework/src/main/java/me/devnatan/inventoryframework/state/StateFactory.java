package me.devnatan.inventoryframework.state;

import java.util.concurrent.atomic.AtomicLong;

public final class StateFactory {

    private static final AtomicLong ids = new AtomicLong();

    public <T> State<T> createState(InternalStateValue value) {
        return new StateImpl<>(nextStateId(), value);
    }

    private static synchronized long nextStateId() {
        return ids.getAndIncrement();
    }
}
