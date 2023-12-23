package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.component.pipeline.ComponentClickInterceptor;
import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.pipeline.ComponentCloseOnClickInterceptor;
import me.devnatan.inventoryframework.pipeline.ContextClickInterceptor;
import me.devnatan.inventoryframework.pipeline.ContextHandleCancelledCloseInterceptor;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform {@link PlatformView} implementation.
 */
@ApiStatus.OverrideOnly
public class View
        extends PlatformView<
                ViewFrame,
                Player,
                BukkitItemComponentBuilder,
                Context,
                OpenContext,
                CloseContext,
                RenderContext,
                SlotClickContext> {

    @Override
    public final @NotNull ElementFactory getElementFactory() {
        return super.getElementFactory();
    }

    @Override
    public final void registerPlatformInterceptors() {
        final Pipeline<? super VirtualView> pipeline = getPipeline();
        pipeline.intercept(PipelinePhase.Component.COMPONENT_CLICK, new ComponentClickInterceptor());
        pipeline.intercept(PipelinePhase.Component.COMPONENT_CLICK, new ComponentCloseOnClickInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_SLOT_CLICK, new ContextClickInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_CLOSE, new ContextHandleCancelledCloseInterceptor());
    }

    @Override
    public final void nextTick(Runnable task) {
        Bukkit.getServer().getScheduler().runTask(getFramework().getOwner(), task);
    }
}
