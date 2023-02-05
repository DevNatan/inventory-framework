package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the rendering phase of a context and renders items on it.
 */
public final class RenderInterceptor implements PipelineInterceptor<IFContext> {

	@Override
	public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
		final RootView root = context.getRoot();
		final int len = context.getContainer().getSize();

		for (int i = 0; i < len; i++) {
			final IFItem<?> item = context.getItem(i);
			if (item == null) {
				root.removeItem(context, i);
				continue;
			}

			root.renderItem(context, item);
		}
	}
}
