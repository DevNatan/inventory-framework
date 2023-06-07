package me.devnatan.inventoryframework.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

@Getter(AccessLevel.PROTECTED)
@SuppressWarnings("unchecked")
abstract class DefaultComponentBuilder<S extends ComponentBuilder<S>> implements ComponentBuilder<S> {

    private String referenceKey;
    private Map<String, Object> data;
    private boolean cancelOnClick, closeOnClick;
    private final Set<State<?>> watching = new LinkedHashSet<>();

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
