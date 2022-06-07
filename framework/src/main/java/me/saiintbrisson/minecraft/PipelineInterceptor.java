package me.saiintbrisson.minecraft;

@FunctionalInterface
public interface PipelineInterceptor<S> {

	void intercept(PipelineContext<S> pipeline, S subject);

}
