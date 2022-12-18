package me.saiintbrisson.minecraft.state;

import org.jetbrains.annotations.NotNull;

public interface MutableState<T> extends State<T> {

    /**
     * Sets a new value for this state in the specified holder.
     *
     * @param value  The new state value.
     * @param holder The state holder that'll get this update.
     */
    void set(T value, @NotNull StateHolder holder);
}
