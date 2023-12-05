package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import org.jetbrains.annotations.NotNull;

public class PlatformComponentHandle {

	void render(@NotNull IFComponentRenderContext context);

	void updated(@NotNull IFComponentUpdateContext context);

	void clicked(@NotNull IFSlotClickContext context);

	void cleared(@NotNull IFRenderContext context);
}
