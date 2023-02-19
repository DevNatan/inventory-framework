package me.devnatan.inventoryframework.state;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class StateImpl<T> implements MutableState<T> {

    private final long id;
    private final InternalStateValue value;

    @SuppressWarnings("unchecked")
    @Override
    public T get(@NotNull StateHost host) {
        final StateHost finalHost = hostFor(host);

        if (!(finalHost instanceof DefaultStateHost))
            throw new IllegalArgumentException("Host must be DefaultStateHost");

        return (T) ((DefaultStateHost) finalHost).get(id, this, value);
    }

    @Override
    public void set(T value, @NotNull StateHost host) {
        if (!(value instanceof MutableValue)) throw new IllegalStateModificationException();

        final StateHost finalHost = hostFor(host);
        if (!(finalHost instanceof DefaultStateHost))
            throw new IllegalArgumentException("Host must be DefaultStateHost");

        ((DefaultStateHost) finalHost).set(id, this.value, value);
    }

    private StateHost hostFor(@NotNull StateHost host) {
        if (host instanceof StateHostAware) return ((StateHostAware) host).getStateHost();

        return host;
    }
}
