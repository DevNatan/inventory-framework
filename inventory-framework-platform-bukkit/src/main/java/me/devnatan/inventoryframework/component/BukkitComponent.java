package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public abstract class BukkitComponent<COMPONENT_BUILDER extends BukkitComponentBuilder<COMPONENT_BUILDER>>
	extends PlatformComponent<Context, COMPONENT_BUILDER> {

	protected BukkitComponent() {
		super(null, null, null, new HashSet<>(), null, null, null, null, false, false, false);
	}

	@Override
	public abstract void render(@NotNull IFComponentRenderContext context);

	@Override
	public void updated(@NotNull IFComponentUpdateContext context) {
	}

	@Override
	public void clicked(@NotNull IFSlotClickContext context) {
	}

	@Override
	public void cleared(@NotNull IFRenderContext context) {
	}
}
