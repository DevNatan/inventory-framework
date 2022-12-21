package me.devnatan.inventoryframework.state.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.state.StateHolder;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
final class MutableState<T> implements me.devnatan.inventoryframework.state.MutableState<T> {

    @Getter
    private final long id;

    @SuppressWarnings("unchecked")
    @Override
    public T get(@NotNull StateHolder holder) {
        return (T) holder.retrieve(id).get();
    }

    @Override
    public void set(T newValue, @NotNull StateHolder holder) {
        holder.retrieve(id).set(newValue);
    }
}
