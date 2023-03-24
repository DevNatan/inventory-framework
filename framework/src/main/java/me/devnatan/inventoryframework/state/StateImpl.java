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

    @SuppressWarnings("unchecked")
    @Override
    public T get(@NotNull StateHost host) {
        final DefaultStateHost finalHost = (DefaultStateHost) hostFor(host);

		T value;
		try {
			value = (T) ((DefaultStateHost) hostFor(host)).get(id);
		} catch (UninitializedStateException exception) {
			finalHost.init(id, valueFactory.apply(finalHost));
			value = (T) ((DefaultStateHost) hostFor(host)).get(id);
		}

        return value;
    }

    @Override
    public void set(T value, @NotNull StateHost host) {
		((DefaultStateHost) hostFor(host)).set(id, value);
    }

    private StateHost hostFor(@NotNull StateHost host) {
        if (host instanceof StateHostAware)
			return ((StateHostAware) host).getStateHost();

        return host;
    }
}
