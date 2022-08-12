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
import org.jetbrains.annotations.TestOnly;

import static me.saiintbrisson.minecraft.IFUtils.elvis;

public class OpenInterceptor implements PipelineInterceptor<OpenViewContext> {

	static final String ASYNC_UPDATE_JOB_EXECUTION_ERROR_MESSAGE = "An error occurred in the opening asynchronous job.";

	@TestOnly
	boolean skipOpen = false;

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
			.thenRun(() -> finishOpen(pipeline, context))
			.exceptionally(error -> {
				System.out.println("async job completed with " + error);

				// TODO invalidate context
				pipeline.finish();
				throw new RuntimeException(ASYNC_UPDATE_JOB_EXECUTION_ERROR_MESSAGE, error);
			});
	}

	private void finishOpen(
		@NotNull PipelineContext<OpenViewContext> pipeline,
		@NotNull OpenViewContext openContext
	) {
		if (skipOpen) return;

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
