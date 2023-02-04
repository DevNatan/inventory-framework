package me.devnatan.inventoryframework.pipeline;

import java.util.List;
import java.util.function.BiConsumer;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.LayoutPattern;

/**
 * Applies items from  user-defined layout patterns to be rendered later by the render interceptor.
 */
public final class LayoutPatternApplierInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext context) {
        final List<LayoutPattern> patterns = context.getLayoutPatterns();
        if (patterns == null) return;

        for (final LayoutPattern pattern : patterns) {
            int iterationIndex = 0;

            for (final int slot : pattern.getSlots()) {
                final IFItem<?> item = pattern.getFactory().apply(iterationIndex++);
                view.apply(item, slot);
            }
        }
    }
}
