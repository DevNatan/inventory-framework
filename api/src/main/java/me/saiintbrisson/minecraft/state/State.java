package me.saiintbrisson.minecraft.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public interface State<T> {

    @ApiStatus.Internal
    long getId();

    T get(@NotNull StateOwner holder);

    void set(T newValue, @NotNull StateOwner holder);
}
