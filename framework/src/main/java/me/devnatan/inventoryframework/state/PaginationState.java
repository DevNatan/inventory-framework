package me.devnatan.inventoryframework.state;

import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;

@ToString(callSuper = true)
public final class PaginationState extends BaseState<Pagination> implements StateManagementListener {

    @Getter
    @Setter
    private boolean lazilyInitialized;

    public PaginationState(long internalId, Function<StateValueHost, StateValue> valueFactory) {
        super(internalId, valueFactory);
    }

    @Override
    public void registered(@NotNull State<?> state, @NotNull StateValueHost host) {
        if (!(host instanceof IFContext))
            throw new IllegalArgumentException("Pagination state can only be registered on IFContext");

        if (isLazilyInitialized()) return;

        setupPipeline(((IFContext) host).getRoot());
        setLazilyInitialized(true);
    }

    @Override
    public void valueGet(
            @NotNull State<?> state,
            @NotNull StateValueHost host,
            @NotNull StateValue internalValue,
            Object rawValue) {}

    @Override
    public void valueSet(
            @NotNull State<?> state,
            @NotNull StateValueHost host,
            @NotNull StateValue internalValue,
            Object rawOldValue,
            Object rawNewValue) {}

    private void setupPipeline(RootView root) {}
}
