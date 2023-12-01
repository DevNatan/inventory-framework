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
    protected Set<State<?>> watchingStates = new HashSet<>();
    protected boolean isManagedExternally;
    protected Predicate<? extends IFContext> displayCondition;
    protected String key;

    protected AbstractComponentBuilder() {}

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
    public SELF key(String key) {
        this.key = key;
        return (SELF) this;
    }

    @Override
    public String toString() {
        return "AbstractComponentBuilder{" + "reference="
                + reference + ", data="
                + data + ", cancelOnClick="
                + cancelOnClick + ", closeOnClick="
                + closeOnClick + ", updateOnClick="
                + updateOnClick + ", watchingStates="
                + watchingStates + ", isManagedExternally="
                + isManagedExternally + ", displayCondition="
                + displayCondition + ", key='"
                + key + '\'' + '}';
    }
}
