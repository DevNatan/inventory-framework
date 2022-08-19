package me.saiintbrisson.minecraft.pipeline.interceptors;

import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.LayoutPattern;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class LayoutPatternRenderInterceptor implements PipelineInterceptor<VirtualView> {

	@Override
	public void intercept(
		@NotNull PipelineContext<VirtualView> pipeline,
		VirtualView subject
	) {
		final List<LayoutPattern> patterns = subject.getLayoutPatterns();
		if (patterns == null || patterns.isEmpty())
			return;

		final boolean applyOnRoot = subject instanceof AbstractView;
		final AbstractView root = applyOnRoot ? (AbstractView) subject : ((ViewContext) subject).getRoot();
		for (final LayoutPattern pattern : patterns) {
			final ViewItem item = pattern.getFactory().get();

			for (final int slot : pattern.getSlots()) {
				if (applyOnRoot) {
					subject.apply(item, slot);
					continue;
				}

				root.render((ViewContext) subject, item, slot);
			}
		}
	}

}
