package me.devnatan.inventoryframework.state;

import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public class BaseState<T> implements State<T> {

    private final long id;
    private final StateValueFactory valueFactory;

    public BaseState(long id, StateValueFactory valueFactory) {
        this.id = id;
        this.valueFactory = valueFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(@NotNull StateValueHost host) {
        return (T) host.getState(this);
    }

    @Override
    public final long internalId() {
        return id;
    }

    @Override
    public final StateValueFactory factory() {
        return valueFactory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseState<?> baseState = (BaseState<?>) o;
        return id == baseState.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BaseState{" + "id=" + id + ", valueFactory=" + valueFactory + '}';
    }
}
