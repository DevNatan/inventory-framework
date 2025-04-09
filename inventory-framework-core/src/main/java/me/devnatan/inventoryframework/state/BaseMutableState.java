package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
public class BaseMutableState<T> extends BaseState<T> implements MutableState<T> {

    public BaseMutableState(long id, StateValueFactory valueFactory) {
        super(id, valueFactory);
    }

    @Override
    public final void set(T value, @NotNull StateValueHost host) {
        host.updateState(this, value);
    }
}
