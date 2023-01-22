package me.devnatan.inventoryframework;

import me.saiintbrisson.minecraft.OpenViewContext;
import me.saiintbrisson.minecraft.ViewRenderContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * Bukkit platform View backward compatible implementation.
 */
@ApiStatus.OverrideOnly
public abstract class View extends PlatformView<ViewContext, OpenViewContext, ViewRenderContext, ViewSlotContext, ViewSlotClickContext> {
	protected View() {
	}

}
