package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

class BaseMutableState<T> extends BaseState<T> implements MutableState<T> {

    protected BaseMutableState(long id, StateValueFactory valueFactory) {
        super(id, valueFactory);
    }

    @Override
    public final void set(T value, @NotNull StateValueHost host) {
        host.updateState(internalId(), value);
    }
}
