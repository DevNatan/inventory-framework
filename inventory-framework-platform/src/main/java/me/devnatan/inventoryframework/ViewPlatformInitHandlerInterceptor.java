package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

final class ViewPlatformInitHandlerInterceptor implements PipelineInterceptor<VirtualView> {

    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
        if (!(view instanceof RootView)) return;

        @SuppressWarnings("rawtypes")
        final PlatformView root = (PlatformView) view;
        ViewConfigBuilder configBuilder = root.createConfig();
        root.onInit(configBuilder);
        root.setConfig(configBuilder.build());
    }
}
