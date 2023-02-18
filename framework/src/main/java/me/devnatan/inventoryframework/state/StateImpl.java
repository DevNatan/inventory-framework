package me.devnatan.inventoryframework.state;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class StateImpl<T> implements State<T> {

    private final long id;
    private final InternalStateValue value;

    @Override
    public T get(@NotNull StateHost host) {
        if (!(host instanceof DefaultStateHost)) throw new IllegalArgumentException("Host must be DefaultStateHost");

        return (T) ((DefaultStateHost) host).get(id, this, value);
    }
}
