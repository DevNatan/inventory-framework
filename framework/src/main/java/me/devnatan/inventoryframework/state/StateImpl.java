package me.devnatan.inventoryframework.state;

import java.util.function.Function;
import lombok.Data;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Data
@ApiStatus.Internal
public final class StateImpl<T> implements MutableState<T> {

    private final long id;
    private final Function<StateHost, InternalStateValue> valueFactory;
    private InternalStateValue currValue;

    @SuppressWarnings("unchecked")
    @Override
    public T get(@NotNull StateHost host) {
        final DefaultStateHost finalHost = (DefaultStateHost) hostFor(host);
        if (currValue == null) {
            currValue = valueFactory.apply(finalHost);
            finalHost.init(id, currValue);
        }

        return (T) finalHost.get(id);
    }

    @Override
    public void set(T value, @NotNull StateHost host) {
        if (!(value instanceof MutableValue)) throw new IllegalStateModificationException();
        currValue.set(value);
    }

    private StateHost hostFor(@NotNull StateHost host) {
        if (host instanceof StateHostAware) return ((StateHostAware) host).getStateHost();

        return host;
    }
}
