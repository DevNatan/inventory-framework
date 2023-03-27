package me.devnatan.inventoryframework.state;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public final class MutableStateImpl<T> extends BaseState<T> implements MutableState<T> {

    public MutableStateImpl(long internalId, Function<StateValueHost, StateValue> valueFactory) {
        super(internalId, valueFactory);
    }

    @Override
    public void set(T value, @NotNull StateValueHost host) {
        getInitialized(host).set(value);
    }
}
