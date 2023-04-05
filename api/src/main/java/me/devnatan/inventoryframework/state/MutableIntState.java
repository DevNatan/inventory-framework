package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface MutableIntState extends MutableState<Integer> {

    int increment(@NotNull StateValueHost host);

    int decrement(@NotNull StateValueHost host);
}
