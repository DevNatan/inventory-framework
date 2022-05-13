package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ViewItemHandler {

	void handle(@NotNull ViewSlotContext context);

}
