package me.devnatan.inventoryframework.state;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public interface StateHolder {

    long generateId();

    StateValueHolder retrieve(long id);

    void updateCaught(@NotNull StateMarker state, Object oldValue, Object newValue);

    StateValueHolder createUnchecked(Object initialValue);

    <T> void watch(@NotNull StateMarker state, @NotNull BiConsumer<T, T> callback);
}
