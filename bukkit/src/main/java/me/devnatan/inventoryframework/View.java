package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.bukkit.BukkitItem;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform {@link PlatformView} implementation.
 */
@ApiStatus.OverrideOnly
public class View
        extends PlatformView<
                BukkitItem, Context, OpenContext, CloseContext, RenderContext, SlotContext, SlotClickContext>
        implements InventoryHolder {

    /**
     * {@inheritDoc}
     */
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

    @NotNull
    @Override
    public final Inventory getInventory() {
        throw new UnsupportedOperationException("Cannot get inventory from RootView");
    }
}
