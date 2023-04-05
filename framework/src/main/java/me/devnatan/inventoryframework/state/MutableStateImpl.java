package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public final class MutableStateImpl<T> extends BaseState<T> implements MutableState<T> {

    public MutableStateImpl(long id, @NotNull StateValueFactory valueFactory) {
        super(id, valueFactory);
    }

    @Override
    public void set(T value, @NotNull StateValueHost host) {
        host.updateState(internalId(), value);
    }
}
