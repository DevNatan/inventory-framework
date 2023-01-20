package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.internal.LayoutPattern;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.VirtualView;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Applies items from {@link VirtualView#setLayout(char, BiConsumer) user-defined layout patterns}
 * to be rendered later by {@link RenderInterceptor}.
 */
public final class LayoutPatternApplierInterceptor implements PipelineInterceptor<VirtualView> {

	@Override
	public void intercept(PipelineContext<VirtualView> pipeline, VirtualView view) {
		final List<LayoutPattern> patterns = view.getLayoutPatterns();
		if (patterns == null) return;

		for (final LayoutPattern pattern : patterns) {
			int iterationIndex = 0;

			for (final int slot : pattern.getSlots()) {
				final IFItem item = pattern.getFactory().apply(iterationIndex++);
				view.apply(item, slot);
			}
		}
	}
}
