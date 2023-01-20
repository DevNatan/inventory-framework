package me.devnatan.inventoryframework.bukkit;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.saiintbrisson.minecraft.OpenViewContext;
import me.saiintbrisson.minecraft.ViewRenderContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Bukkit platform View backward compatible implementation.
 */
@ApiStatus.OverrideOnly
public abstract class View extends PlatformView<ViewContext, OpenViewContext, ViewRenderContext, ViewSlotContext, ViewSlotClickContext> {
	protected View() {
	}

}
