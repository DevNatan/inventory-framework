package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class MutableGenericStateImpl<T> extends BaseState<T> implements MutableState<T> {

    public MutableGenericStateImpl(long id, @NotNull StateValueFactory valueFactory) {
        super(id, valueFactory);
    }

    @Override
    public void set(T value, @NotNull StateValueHost host) {
        host.updateState(this, value);
    }
}
