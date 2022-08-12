package me.saiintbrisson.minecraft.pipeline;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PipelineInterceptor<S> {

	void intercept(@NotNull PipelineContext<S> pipeline, S subject);

}
