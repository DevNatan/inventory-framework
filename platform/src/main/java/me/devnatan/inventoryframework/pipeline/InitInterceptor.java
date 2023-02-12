package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public final class InitInterceptor implements PipelineInterceptor<PlatformView> {

    public void intercept(@NotNull PipelineContext<PlatformView> pipeline, PlatformView view) {
        ViewConfigBuilder configBuilder = view.createConfig();
        view.onInit(configBuilder);
        view.setConfig(configBuilder.build());
    }
}
