package me.devnatan.inventoryframework.state;

import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class InitialDataStateValue extends StateValue {

    private final StateValue backingValue;

    public InitialDataStateValue(@NotNull State<?> state, @NotNull StateValueHost host) {
        super(state);
        if (!(host instanceof IFContext))
            throw new IllegalArgumentException("State host for initial data must be a IFContext");

        this.backingValue = new LazyValue(state, () -> {
            throw new UnsupportedOperationException("Initial data state is not supported yet");
        });
    }

    @Override
    @UnknownNullability
    Object get() {
        return backingValue.get();
    }

    @Override
    void set(Object value) {
        backingValue.set(value);
    }
}
