package me.saiintbrisson.minecraft;

@FunctionalInterface
public interface PipelineInterceptor<S> {

	void intercept(PipelineContext<S> context, S subject);

}
