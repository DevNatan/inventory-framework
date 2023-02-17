package me.devnatan.inventoryframework.internal.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.state.StateHost;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
final class MutableState<T> implements me.devnatan.inventoryframework.state.MutableState<T> {

    @Getter
    private final long id;

    @SuppressWarnings("unchecked")
    @Override
    public T get(@NotNull StateHost holder) {
        return (T) holder.retrieve(id).get();
    }

    @Override
    public void set(T newValue, @NotNull StateHost holder) {
        holder.retrieve(id).set(newValue);
    }
}
