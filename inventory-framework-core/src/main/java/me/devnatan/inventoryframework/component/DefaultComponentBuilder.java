package me.devnatan.inventoryframework.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
abstract class DefaultComponentBuilder<S extends ComponentBuilder<S, C>, C extends IFContext>
        implements ComponentBuilder<S, C> {

    protected Ref<Component> reference;
    protected Map<String, Object> data;
    protected boolean cancelOnClick, closeOnClick, updateOnClick;
    protected Set<State<?>> watchingStates;
    protected boolean isManagedExternally;
    protected Predicate<C> displayCondition;

    protected DefaultComponentBuilder(
            Ref<Component> reference,
            Map<String, Object> data,
            boolean cancelOnClick,
            boolean closeOnClick,
            boolean updateOnClick,
            Set<State<?>> watchingStates,
            boolean isManagedExternally,
            Predicate<C> displayCondition) {
        this.reference = reference;
        this.data = data;
        this.cancelOnClick = cancelOnClick;
        this.closeOnClick = closeOnClick;
        this.updateOnClick = updateOnClick;
        this.watchingStates = watchingStates;
        this.isManagedExternally = isManagedExternally;
        this.displayCondition = displayCondition;
    }

    @Override
    public S referencedBy(@NotNull Ref<Component> reference) {
        this.reference = reference;
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
    public S updateOnStateChange(@NotNull State<?> state) {
        if (watchingStates == null) watchingStates = new LinkedHashSet<>();
        watchingStates.add(state);
        return (S) this;
    }

    @Override
    public S updateOnStateChange(State<?>... states) {
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
        this.displayCondition = displayCondition == null ? null : $ -> displayCondition.getAsBoolean();
        return (S) this;
    }

    @Override
    public S displayIf(Predicate<C> displayCondition) {
        this.displayCondition = displayCondition;
        return (S) this;
    }

    @Override
    public S hideIf(Predicate<C> condition) {
        return displayIf(condition == null ? null : arg -> !condition.test(arg));
    }

    @Override
    public S hideIf(BooleanSupplier condition) {
        return displayIf(condition == null ? null : () -> !condition.getAsBoolean());
    }
}
