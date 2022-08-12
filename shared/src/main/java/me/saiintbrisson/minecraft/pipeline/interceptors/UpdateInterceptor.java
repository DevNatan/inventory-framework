package me.saiintbrisson.minecraft.pipeline.interceptors;

import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the update phase of a context.
 */
public final class UpdateInterceptor implements PipelineInterceptor<VirtualView> {

	@Override
	public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
		if (!(view instanceof ViewContext))
			throw new IllegalStateException("Update interceptor must be called with a context as subject.");

		final ViewContext context = (ViewContext) view;
		context.inventoryModificationTriggered();

		final AbstractView root = context.getRoot();

		// we prioritize the amount of items in the context because the priority is in the context
		// and the size of items in the context is proportional to the size of its container
		final int len = context.getItems().length;

		for (int i = 0; i < len; i++) {
			context.inventoryModificationTriggered();

			// this resolve will get the items from both root and context so we render both
			final ViewItem item = root.resolve(i, true);

			if (item == null) {
				context.getContainer().removeItem(i);
				return;
			}

			root.update(context, item, i);
		}
	}

}
