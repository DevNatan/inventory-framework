package me.devnatan.inventoryframework.state;

import java.util.Map;
import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class InitialDataStateValue extends AbstractStateValue {

    private final IFContext context;
    private final String key;
    private StateValue backingValue;

    public InitialDataStateValue(long internalId, @NotNull StateValueHost host, String key) {
        super(internalId);
        this.key = key;
        if (!(host instanceof IFContext))
            throw new IllegalArgumentException("StatePhase host for initial data must be a IFContext");

        this.context = (IFContext) host;
        this.backingValue = createBackingValue(internalId, (IFContext) host, key);
    }

    @SuppressWarnings("unchecked")
    private StateValue createBackingValue(long internalId, IFContext context, String key) {
        return new LazyValue(
                internalId,
                () -> key != null && context.getInitialData() instanceof Map
                        ? ((Map<String, ?>) context.getInitialData()).get(key)
                        : context.getInitialData());
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

    public void reset() {
        backingValue = createBackingValue(internalId(), context, key);
    }
}
