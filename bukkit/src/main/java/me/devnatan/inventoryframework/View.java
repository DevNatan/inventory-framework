package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.context.SlotContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.pipeline.GlobalClickInterceptor;
import me.devnatan.inventoryframework.pipeline.ItemClickInterceptor;
import me.devnatan.inventoryframework.pipeline.ItemCloseOnClickInterceptor;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import org.jetbrains.annotations.ApiStatus;

/**
 * Bukkit platform View backward compatible implementation.
 */
@ApiStatus.OverrideOnly
public class View
        extends PlatformView<
                BukkitItem, Context, OpenContext, CloseContext, RenderContext, SlotContext, SlotClickContext> {

    /** {@inheritDoc} */
    @Override
    final void internalInitialization() {
        super.internalInitialization();

        final Pipeline<? super VirtualView> pipeline = getPipeline();
        pipeline.intercept(StandardPipelinePhases.CLICK, new GlobalClickInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLICK, new ItemClickInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLICK, new ItemCloseOnClickInterceptor());
    }

    @Override
    public final ElementFactory getElementFactory() {
        return super.getElementFactory();
    }
}
