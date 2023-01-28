package me.devnatan.inventoryframework.pipeline;

import java.util.ArrayList;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.internal.PlatformUtils;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
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

        final RootView root = openContext.getRoot();
        final int containerSize = openContext.getType().normalize(openContext.getSize());
        final ViewContainer container = PlatformUtils.getFactory()
                .createContainer(root, containerSize, openContext.getTitle(), openContext.getType());

        final IFContext generatedContext = PlatformUtils.getFactory().createContext(root, container, null, null);

        //        generatedContext.setItems(new IFItem[containerSize]);
        //        generatedContext.setPrevious(((BaseViewContext) openContext).getPrevious());

        for (final Viewer viewer : openContext.getViewers()) generatedContext.addViewer(viewer);

        root.addContext(generatedContext);
        root.renderContext(generatedContext);

        for (final Viewer viewer : new ArrayList<>(generatedContext.getViewers())) {
            container.open(viewer);
        }
    }
}
