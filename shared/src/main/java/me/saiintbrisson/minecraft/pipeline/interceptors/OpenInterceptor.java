package me.saiintbrisson.minecraft.pipeline.interceptors;

import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.OpenViewContext;
import me.saiintbrisson.minecraft.PlatformUtils;
import me.saiintbrisson.minecraft.ViewContainer;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.ViewType;
import me.saiintbrisson.minecraft.Viewer;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

import static me.saiintbrisson.minecraft.IFUtils.elvis;

public final class OpenInterceptor implements PipelineInterceptor<OpenViewContext> {

	@Override
	public void intercept(
		@NotNull PipelineContext<OpenViewContext> pipeline,
		OpenViewContext context
	) {
		if (context.getAsyncOpenJob() == null) {
			finishOpen(pipeline, context);
			return;
		}

		context.getAsyncOpenJob()
			.whenComplete(($, error) -> finishOpen(pipeline, context))
			.exceptionally(error -> {
				// TODO invalidate context
				pipeline.finish();
				throw new RuntimeException("An error occurred in the opening asynchronous job.", error);
			});
	}

	private void finishOpen(
		@NotNull PipelineContext<OpenViewContext> pipeline,
		@NotNull OpenViewContext openContext
	) {
		if (openContext.isCancelled()) {
			pipeline.finish();
			return;
		}

		final AbstractView root = openContext.getRoot();
		final String containerTitle = elvis(openContext.getContainerTitle(), root.getTitle());
		final ViewType containerType = elvis(openContext.getContainerType(), root.getType());

		// rows will be normalized to fixed container size on `createContainer`
		final int containerSize = openContext.getContainerSize() == 0
			? root.getSize()
			: containerType.normalize(openContext.getContainerSize());

		final ViewContainer container = PlatformUtils.getFactory()
			.createContainer(root, containerSize, containerTitle, containerType);

		final ViewContext generatedContext = PlatformUtils.getFactory()
			.createContext(root, container, null);

		generatedContext.setItems(new ViewItem[containerSize]);
		for (final Viewer viewer : openContext.getViewers())
			generatedContext.addViewer(viewer);

		// ensure data inheritance from open context to lifecycle context
		openContext.getData().forEach(generatedContext::set);
		root.registerContext(generatedContext);
		root.render(generatedContext);
		generatedContext.getViewers().forEach(container::open);
	}

}
