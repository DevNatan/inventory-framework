package me.devnatan.inventoryframework.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@RequiredArgsConstructor
@Getter(AccessLevel.NONE)
public class BaseState<T> implements State<T> {

    private final long id;

    @EqualsAndHashCode.Exclude
    private final StateValueFactory valueFactory;

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
}
