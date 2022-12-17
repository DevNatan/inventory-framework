package me.saiintbrisson.minecraft.state.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.saiintbrisson.minecraft.state.State;
import me.saiintbrisson.minecraft.state.StateOwner;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
final class MutableState<T> implements State<T> {

    @Getter
    private final long id;

    @SuppressWarnings("unchecked")
    @Override
    public T get(@NotNull StateOwner holder) {
        return (T) holder.retrieve(id).get();
    }

    @Override
    public void set(T newValue, @NotNull StateOwner holder) {
        holder.retrieve(id).set(newValue);
    }
}
