package me.saiintbrisson.minecraft.pipeline.interceptors;

import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the rendering phase of a context and renders {@link VirtualView#slot(int) statically defined items}
 * in it and its root.
 */
public final class RenderInterceptor implements PipelineInterceptor<VirtualView> {

	@Override
	public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
		if (!(view instanceof ViewContext))
			throw new IllegalStateException("Render interceptor must be called with a context as subject.");

		System.out.println("On render interceptor");
		final ViewContext context = (ViewContext) view;
		context.inventoryModificationTriggered();

		final AbstractView root = context.getRoot();

		// we prioritize the amount of items in the context because the priority is in the context
		// and the size of items in the context is proportional to the size of its container
		final int len = context.getItems().length;
		System.out.println("Items len: " + len);

		for (int i = 0; i < len; i++) {
			// this resolve will get the items from both root and context, so we render both
			final ViewItem item = context.resolve(i, true);
			System.out.println("Item at " + i + ": " + item);

			if (item == null)
				continue;

			System.out.println("Rendering " + i + ": " + item);
			root.render(context, item, i);
		}
	}

}
