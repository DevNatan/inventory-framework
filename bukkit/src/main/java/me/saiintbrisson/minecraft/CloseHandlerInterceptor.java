package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted on close, calls Bukkit platform view {@link View#onClose(ViewContext) close handler}.
 */
final class CloseHandlerInterceptor implements PipelineInterceptor<ViewContext> {

    @Override
    public void intercept(@NotNull PipelineContext<ViewContext> pipeline, @NotNull ViewContext context) {
		((View) context.getRoot()).onClose(context);
    }
}
