package me.saiintbrisson.minecraft.pipeline.interceptors;

import java.util.List;
import java.util.function.BiConsumer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.saiintbrisson.minecraft.LayoutPattern;
import me.devnatan.inventoryframework.ViewItem;
import org.jetbrains.annotations.NotNull;

/**
 * Applies items from {@link VirtualView#setLayout(char, BiConsumer) user-defined layout patterns}
 * to be rendered later by {@link RenderInterceptor}.
 */
public final class LayoutPatternApplierInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
        final List<LayoutPattern> patterns = view.getLayoutPatterns();
        if (patterns == null) return;

        for (final LayoutPattern pattern : patterns) {
            int iterationIndex = 0;

            for (final int slot : pattern.getSlots()) {
                final ViewItem item = pattern.getFactory().apply(iterationIndex++);
                view.apply(item, slot);
            }
        }
    }
}
