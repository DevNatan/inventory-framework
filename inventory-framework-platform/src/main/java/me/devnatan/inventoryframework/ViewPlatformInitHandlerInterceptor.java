package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
final class ViewPlatformInitHandlerInterceptor implements PipelineInterceptor<PlatformView> {

    public void intercept(@NotNull PipelineContext<PlatformView> pipeline, PlatformView view) {
        ViewConfigBuilder configBuilder = view.createConfig();
        view.onInit(configBuilder);
        view.setConfig(configBuilder.build());
    }
}
