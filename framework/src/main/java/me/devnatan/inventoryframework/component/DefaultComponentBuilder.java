package me.devnatan.inventoryframework.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
abstract class DefaultComponentBuilder<S extends ComponentBuilder<S>> implements ComponentBuilder<S> {

    protected String referenceKey;
    protected Map<String, Object> data;
    protected boolean cancelOnClick, closeOnClick;
    protected final Set<State<?>> watching = new LinkedHashSet<>();
    protected boolean isManagedExternally;

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
    public S watch(State<?>... states) {
        watching.addAll(Arrays.asList(states));
        return (S) this;
    }

    @Override
    public S withExternallyManaged(boolean isExternallyManaged) {
        isManagedExternally = isExternallyManaged;
        return (S) this;
    }
}
