package me.saiintbrisson.minecraft;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ToString
@RequiredArgsConstructor
public class PipelinePhaseContent<S, C> {

	@NotNull
	private final PipelinePhase phase;
	@NotNull
	private final PipelineRelation relation;

	private final List<PipelineInterceptor<S, C>> interceptors;

	public void addInterceptor(
		final @NotNull PipelineInterceptor<S, C> interceptor
	) {
		interceptors.add(interceptor);
	}

}
