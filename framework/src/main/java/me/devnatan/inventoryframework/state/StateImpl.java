package me.devnatan.inventoryframework.state;

import java.util.function.Function;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Data
final class StateImpl<T> implements State<T> {

    @Accessors(fluent = true)
    private final long internalId;

    private final Function<StateValueHost, StateValue> valueFactory;

    @SuppressWarnings("unchecked")
    @Override
    public T get(@NotNull StateValueHost host) {
        // TODO UninitializedStateException for unknown id (maybe late init?)
        return (T) host.getState(internalId()).get();
    }
}
