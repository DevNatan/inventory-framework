package me.saiintbrisson.minecraft.pipeline.interceptors;

import java.util.List;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.LayoutPattern;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public final class LayoutPatternRenderInterceptor implements PipelineInterceptor<VirtualView> {

    @TestOnly
    public boolean skipRender = false;

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
        final List<LayoutPattern> patterns = view.getLayoutPatterns();
        if (patterns == null || patterns.isEmpty()) return;

        final boolean applyOnRoot = view instanceof AbstractView;
        final AbstractView root = applyOnRoot ? (AbstractView) view : ((ViewContext) view).getRoot();
        for (final LayoutPattern pattern : patterns) {
            final ViewItem item = pattern.getFactory().get();
            System.out.println("called pattern get factory " + item);

            if (skipRender) continue;

            for (final int slot : pattern.getSlots()) {
                if (applyOnRoot) {
                    view.apply(item, slot);
                    continue;
                }

                view.apply(item, slot);
                root.render((ViewContext) view, item, slot);
            }
        }
    }
}
