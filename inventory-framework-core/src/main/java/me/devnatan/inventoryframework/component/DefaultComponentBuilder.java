package me.devnatan.inventoryframework.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
abstract class DefaultComponentBuilder<S extends ComponentBuilder<S>> implements ComponentBuilder<S> {

    protected String referenceKey;
    protected Map<String, Object> data;
    protected boolean cancelOnClick, closeOnClick, updateOnClick;
    protected Set<State<?>> watchingStates;
    protected boolean isManagedExternally;
    protected BooleanSupplier displayCondition;

    protected DefaultComponentBuilder(
            String referenceKey,
            Map<String, Object> data,
            boolean cancelOnClick,
            boolean closeOnClick,
            boolean updateOnClick,
            Set<State<?>> watchingStates,
            boolean isManagedExternally,
            BooleanSupplier displayCondition) {
        this.referenceKey = referenceKey;
        this.data = data;
        this.cancelOnClick = cancelOnClick;
        this.closeOnClick = closeOnClick;
        this.updateOnClick = updateOnClick;
        this.watchingStates = watchingStates;
        this.isManagedExternally = isManagedExternally;
        this.displayCondition = displayCondition;
    }

    @Override
    public S referencedBy(@NotNull String key) {
        this.referenceKey = key;
        return (S) this;
    }

    @Override
    public S withData(@NotNull String key, Object value) {
        if (data == null) data = new HashMap<>();
        data.put(key, value);
        return (S) this;
    }

    @Override
    public S cancelOnClick() {
        cancelOnClick = !cancelOnClick;
        return (S) this;
    }

    @Override
    public S closeOnClick() {
        closeOnClick = !closeOnClick;
        return (S) this;
    }

    @Override
    public S updateOnClick() {
        updateOnClick = !updateOnClick;
        return (S) this;
    }

    @Override
    public S watch(State<?>... states) {
        if (watchingStates == null) watchingStates = new LinkedHashSet<>();
        watchingStates.addAll(Arrays.asList(states));
        return (S) this;
    }

    @Override
    public S withExternallyManaged(boolean isExternallyManaged) {
        isManagedExternally = isExternallyManaged;
        return (S) this;
    }

    @Override
    public S displayIf(BooleanSupplier displayCondition) {
        this.displayCondition = displayCondition;
        return (S) this;
    }
}
