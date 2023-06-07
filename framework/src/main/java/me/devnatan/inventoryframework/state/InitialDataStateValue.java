package me.devnatan.inventoryframework.state;

import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class InitialDataStateValue extends StateValue {

    private final StateValue backingValue;

    public InitialDataStateValue(@NotNull State<?> state, @NotNull StateValueHost host, @NotNull String key) {
        super(state);
        if (!(host instanceof IFContext))
            throw new IllegalArgumentException("State host for initial data must be a IFContext");

        final IFContext context = (IFContext) host;
        this.backingValue = new LazyValue(state, () -> context.getInitialData().get(key));
    }

    @Override
    @UnknownNullability
    public Object get() {
        return backingValue.get();
    }

    @Override
    public void set(Object value) {
        backingValue.set(value);
    }
}
