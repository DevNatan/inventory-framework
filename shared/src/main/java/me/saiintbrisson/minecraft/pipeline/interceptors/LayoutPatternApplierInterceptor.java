package me.saiintbrisson.minecraft.pipeline.interceptors;

import java.util.List;
import java.util.function.Consumer;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.LayoutPattern;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

/**
 * Applies items from {@link VirtualView#setLayout(char, Consumer)  user-defined layout patterns}
 * to be rendered later by {@link RenderInterceptor}.
 */
public final class LayoutPatternApplierInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
        final List<LayoutPattern> patterns = view.getLayoutPatterns();
        if (patterns == null || patterns.isEmpty()) return;

        final boolean applyOnRoot = view instanceof AbstractView;
        final AbstractView root = applyOnRoot ? (AbstractView) view : ((ViewContext) view).getRoot();
        for (final LayoutPattern pattern : patterns) {
            final ViewItem item = pattern.getFactory().get();

            for (final int slot : pattern.getSlots()) (applyOnRoot ? root : view).apply(item, slot);
        }
    }
}
