package me.devnatan.inventoryframework.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public abstract class DefaultComponentBuilder<S extends ComponentBuilder<S, C>, C extends IFContext>
        implements ComponentBuilder<S, C> {

	protected Function<? extends IFContext, String> keyFactory;
    protected Ref<Component> reference;
    protected Map<String, Object> data;
    protected boolean cancelOnClick, closeOnClick, updateOnClick;
    protected Set<State<?>> watchingStates;
    protected boolean isManagedExternally;
    protected Predicate<C> displayCondition;

    protected DefaultComponentBuilder(
			Function<? extends IFContext, String> keyFactory,
            Ref<Component> reference,
            Map<String, Object> data,
            boolean cancelOnClick,
            boolean closeOnClick,
            boolean updateOnClick,
            Set<State<?>> watchingStates,
            boolean isManagedExternally,
            Predicate<C> displayCondition) {
		this.keyFactory = keyFactory;
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
    public S updateOnStateChange(@NotNull State<?> state, State<?>... states) {
        if (watchingStates == null) watchingStates = new LinkedHashSet<>();
        watchingStates.add(state);
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

	@Override
	public S identifiedBy(String key) {
		return identifiedBy(() -> key);
	}

	@Override
	public S identifiedBy(Supplier<String> key) {
		return identifiedBy(__ -> key.get());
	}

	@Override
	public S identifiedBy(Function<C, String> keyFactory) {
		this.keyFactory = keyFactory;
		return (S) this;
	}
}
