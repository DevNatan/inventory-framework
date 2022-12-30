package me.devnatan.inventoryframework.pipeline;

@FunctionalInterface
public interface PipelineInterceptor<S> {

	void intercept(PipelineContext<S> pipeline, S subject);
}
