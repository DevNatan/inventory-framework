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

    private String referenceKey;
    private Map<String, Object> data;
    private boolean cancelOnClick, closeOnClick;
    private final Set<State<?>> watching = new LinkedHashSet<>();

    protected final String getReferenceKey() {
        return referenceKey;
    }

    protected final Map<String, Object> getData() {
        return data;
    }

    protected final boolean isCancelOnClick() {
        return cancelOnClick;
    }

    protected final boolean isCloseOnClick() {
        return closeOnClick;
    }

    protected final Set<State<?>> getWatching() {
        return watching;
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
    public S watch(State<?>... states) {
        watching.addAll(Arrays.asList(states));
        return (S) this;
    }
}
