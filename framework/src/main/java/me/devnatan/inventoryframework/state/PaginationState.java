package me.devnatan.inventoryframework.state;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.component.Pagination;
import org.jetbrains.annotations.NotNull;

@ToString(callSuper = true)
public final class PaginationState extends BaseState<Pagination> implements StateManagementListener {

    @Getter
    @Setter
    private boolean lazilyInitialized;

    public PaginationState(long id, @NotNull StateValueFactory valueFactory) {
        super(id, valueFactory);
    }

    @Override
    public void stateRegistered(@NotNull State<?> state, Object caller) {
        if (!(caller instanceof RootView))
            throw new IllegalArgumentException("Pagination state can only be registered on RootView");

        if (isLazilyInitialized()) return;

        final RootView root = (RootView) caller;
        setLazilyInitialized(true);
    }

    @Override
    public void stateUnregistered(@NotNull State<?> state) {}

    @Override
    public void stateValueInitialized(@NotNull StateValueHost host, @NotNull StateValue value, Object initialValue) {
        System.out.println("pagination value initialized at " + host);
    }

    @Override
    public void stateValueGet(
            @NotNull State<?> state, @NotNull StateValueHost host, @NotNull StateValue internalValue, Object rawValue) {
        System.out.println("tried to get pagination value");
    }

    @Override
    public void stateValueSet(
            @NotNull StateValueHost host, @NotNull StateValue value, Object rawOldValue, Object rawNewValue) {}
}
