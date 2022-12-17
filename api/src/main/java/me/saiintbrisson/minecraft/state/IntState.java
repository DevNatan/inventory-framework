package me.saiintbrisson.minecraft.state;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public interface IntState extends State<Integer> {

    void increment(@NotNull StateOwner target);

    void decrement(@NotNull StateOwner target);
}
