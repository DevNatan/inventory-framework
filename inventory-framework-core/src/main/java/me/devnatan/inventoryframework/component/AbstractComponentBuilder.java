package me.devnatan.inventoryframework.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
public abstract class AbstractComponentBuilder<SELF extends ComponentBuilder<SELF>> implements ComponentBuilder<SELF> {

    protected Ref<Component> reference;
    protected Map<String, Object> data;
    protected boolean cancelOnClick, closeOnClick, updateOnClick;
    protected Set<State<?>> watchingStates;
    protected boolean isManagedExternally;
    protected Predicate<? extends IFContext> displayCondition;

	protected AbstractComponentBuilder() {
		this(null, new HashMap<>(), false, false, false, new HashSet<>(), false, null);
	}

    protected AbstractComponentBuilder(
            Ref<Component> reference,
            Map<String, Object> data,
            boolean cancelOnClick,
            boolean closeOnClick,
            boolean updateOnClick,
            Set<State<?>> watchingStates,
            boolean isManagedExternally,
			Predicate<? extends IFContext> displayCondition) {
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
    public SELF referencedBy(@NotNull Ref<Component> reference) {
        this.reference = reference;
        return (SELF) this;
    }

    @Override
    public SELF withData(@NotNull String key, Object value) {
        if (data == null) data = new HashMap<>();
        data.put(key, value);
        return (SELF) this;
    }

    @Override
    public SELF cancelOnClick() {
        cancelOnClick = !cancelOnClick;
        return (SELF) this;
    }

    @Override
    public SELF closeOnClick() {
        closeOnClick = !closeOnClick;
        return (SELF) this;
    }

    @Override
    public SELF updateOnClick() {
        updateOnClick = !updateOnClick;
        return (SELF) this;
    }

    @Override
    public SELF watch(State<?>... states) {
        if (watchingStates == null) watchingStates = new LinkedHashSet<>();
        watchingStates.addAll(Arrays.asList(states));
        return (SELF) this;
    }

    @Override
    public SELF updateOnStateChange(@NotNull State<?> state) {
        if (watchingStates == null) watchingStates = new LinkedHashSet<>();
        watchingStates.add(state);
        return (SELF) this;
    }

    @Override
    public SELF updateOnStateChange(@NotNull State<?> state, State<?>... states) {
        if (watchingStates == null) watchingStates = new LinkedHashSet<>();
        watchingStates.add(state);
        watchingStates.addAll(Arrays.asList(states));
        return (SELF) this;
    }

    @Override
    public SELF withExternallyManaged(boolean isExternallyManaged) {
        isManagedExternally = isExternallyManaged;
        return (SELF) this;
    }

    @Override
    public SELF displayIf(BooleanSupplier displayCondition) {
        this.displayCondition = displayCondition == null ? null : $ -> displayCondition.getAsBoolean();
        return (SELF) this;
    }

    @Override
    public SELF hideIf(BooleanSupplier condition) {
        return displayIf(condition == null ? null : () -> !condition.getAsBoolean());
    }

	@Override
	public SELF copy() {
		throw new UnsupportedOperationException("Component builder not copyable - missing #copy() impl");
	}
}
