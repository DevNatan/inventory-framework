package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.pipeline.GlobalClickInterceptor;
import me.devnatan.inventoryframework.pipeline.ItemClickInterceptor;
import me.devnatan.inventoryframework.pipeline.ItemCloseOnClickInterceptor;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import org.jetbrains.annotations.ApiStatus;

/**
 * Bukkit platform View backward compatible implementation.
 */
@ApiStatus.OverrideOnly
public class View extends PlatformView<
	BukkitItem, ViewContext, ViewOpenContext, ViewRenderContext, SlotContext, SlotClickContext> {

    /** {@inheritDoc} */
    @Override
	final void internalInitialization() {
        super.internalInitialization();

        final Pipeline<? super VirtualView> pipeline = getPipeline();
        pipeline.intercept(CLICK, new GlobalClickInterceptor());
        pipeline.intercept(CLICK, new ItemClickInterceptor());
        pipeline.intercept(CLICK, new ItemCloseOnClickInterceptor());
    }
}
