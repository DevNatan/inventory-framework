package me.saiintbrisson.minecraft;

@FunctionalInterface
public interface PipelineInterceptor<S, C> {

	void intercept(PipelineContext<S, C> context, S subject);

}
