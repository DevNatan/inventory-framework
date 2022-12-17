package me.saiintbrisson.minecraft.pipeline.interceptors;

import static me.saiintbrisson.minecraft.IFUtils.elvis;

import java.util.ArrayList;
import me.devnatan.inventoryframework.VirtualView;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.BaseViewContext;
import me.saiintbrisson.minecraft.OpenViewContext;
import me.saiintbrisson.minecraft.PlatformUtils;
import me.saiintbrisson.minecraft.ViewContainer;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.ViewType;
import me.saiintbrisson.minecraft.Viewer;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public class OpenInterceptor implements PipelineInterceptor<VirtualView> {

    @TestOnly
    boolean skipOpen = false;

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView context) {
        if (!(context instanceof OpenViewContext)) return;

        final OpenViewContext openContext = (OpenViewContext) context;
        if (openContext.getAsyncOpenJob() == null) {
            finishOpen(pipeline, openContext);
            return;
        }

        openContext
                .getAsyncOpenJob()
                .thenRun(() -> finishOpen(pipeline, openContext))
                .exceptionally(error -> {
                    // TODO invalidate context
                    pipeline.finish();
                    throw new IllegalStateException("An error occurred in the opening asynchronous job.", error);
                });
    }

    private void finishOpen(@NotNull PipelineContext<VirtualView> pipeline, @NotNull OpenViewContext openContext) {
        if (openContext.isCancelled()) {
            pipeline.finish();
            return;
        }

        if (skipOpen) return;

        final AbstractView root = openContext.getRoot();
        final String containerTitle = elvis(openContext.getContainerTitle(), root.getTitle());
        final ViewType containerType = elvis(openContext.getContainerType(), root.getType());

        // rows will be normalized to fixed container size on `createContainer`
        final int containerSize = openContext.getContainerSize() == 0
                ? root.getSize()
                : containerType.normalize(openContext.getContainerSize());

        final ViewContainer container =
                PlatformUtils.getFactory().createContainer(root, containerSize, containerTitle, containerType);

        final BaseViewContext generatedContext = PlatformUtils.getFactory().createContext(root, container, null);

        generatedContext.setItems(new ViewItem[containerSize]);
        generatedContext.setPrevious(openContext.getPrevious());

        for (final Viewer viewer : openContext.getViewers()) generatedContext.addViewer(viewer);

        // ensure data inheritance from open context to lifecycle context
        openContext.getData().forEach(generatedContext::set);
        root.registerContext(generatedContext);
        root.render(generatedContext);

        for (final Viewer viewer : new ArrayList<>(generatedContext.getViewers())) {
            container.open(viewer);
        }
    }
}
