package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class HistoryImpl extends AbstractStateValue implements History {

    private int index = 0;

    public HistoryImpl(@NotNull State<?> state) {
        super(state);
    }

    @Override
    public @UnknownNullability Object get() {
        return this;
    }

    @Override
    public void rewind() {}

    @Override
    public void pop() {}
}
