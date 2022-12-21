package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public interface StateHolder {

    long generateId();

    StateValueHolder retrieve(long id);

    void updateCaught(@NotNull State<?> state, Object oldValue, Object newValue);

    StateValueHolder createUnchecked(Object initialValue);
}
