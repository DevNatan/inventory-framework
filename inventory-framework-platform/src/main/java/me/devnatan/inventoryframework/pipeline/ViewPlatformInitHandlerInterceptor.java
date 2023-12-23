package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.VirtualView;
import org.jetbrains.annotations.NotNull;

public final class ViewPlatformInitHandlerInterceptor implements PipelineInterceptor<VirtualView> {

    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
        if (!(view instanceof RootView)) return;

        @SuppressWarnings("rawtypes")
        final PlatformView root = (PlatformView) view;
        ViewConfigBuilder configBuilder = root.createConfig();
        root.onInit(configBuilder);
        root.setConfig(configBuilder.build());
    }
}
