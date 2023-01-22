package me.devnatan.inventoryframework.pipeline;

import java.util.ArrayList;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.BaseViewContext;
import me.saiintbrisson.minecraft.PlatformUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public class OpenInterceptor implements PipelineInterceptor<VirtualView> {

    @TestOnly
    boolean skipOpen = false;

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView context) {
        if (!(context instanceof IFOpenContext)) return;

        final IFOpenContext openContext = (IFOpenContext) context;
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

    private void finishOpen(@NotNull PipelineContext<VirtualView> pipeline, @NotNull IFOpenContext openContext) {
        if (openContext.isCancelled()) {
            pipeline.finish();
            return;
        }

        if (skipOpen) return;

        final AbstractView root = openContext.getRoot();
        final String containerTitle = IFUtils.elvis(openContext.getTitle(), root.getTitle());
        final ViewType containerType = IFUtils.elvis(openContext.getType(), root.getType());

        // rows will be normalized to fixed container size on `createContainer`
        final int containerSize =
                openContext.getSize() == 0 ? root.getSize() : containerType.normalize(openContext.getSize());

        final ViewContainer container =
                PlatformUtils.getFactory().createContainer(root, containerSize, containerTitle, containerType);

        final BaseViewContext generatedContext = PlatformUtils.getFactory().createContext(root, container, null, null);

        generatedContext.setItems(new IFItem[containerSize]);
        generatedContext.setPrevious(((BaseViewContext) openContext).getPrevious());

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
