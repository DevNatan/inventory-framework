package me.devnatan.inventoryframework.state;

import java.util.function.Function;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Data
public class BaseState<T> implements State<T> {

    @Accessors(fluent = true)
    private final long internalId;

    @EqualsAndHashCode.Exclude
    private final Function<StateValueHost, StateValue> valueFactory;

    @SuppressWarnings("unchecked")
    @Override
    public T get(@NotNull StateValueHost host) {
        return (T) getInitialized(host).get();
    }

    protected final StateValue getInitialized(@NotNull StateValueHost host) {
        try {
            return host.getState(internalId());
        } catch (final UninitializedStateException e) {
            final StateValue resultValue = valueFactory.apply(host);
            host.setState(internalId(), resultValue);
            return resultValue;
        }
    }
}
