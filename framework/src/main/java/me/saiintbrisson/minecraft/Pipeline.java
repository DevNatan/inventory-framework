package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The pipeline is a structure containing a sequence of functions (blocks/lambdas) that are called
 * one after another, distributed in phases topologically ordered, with the ability to mutate the
 * sequence and to call the remaining functions in the pipeline and then return to current block.
 * <p>
 * All the functions are blocking, thus the whole pipeline is synchronous. Since pipelines contain
 * blocks of code, they can be nested, effectively creating sub-pipelines.
 * <p>
 * Pipelines are used in IF as an extension mechanism to plug functionality in at the right place.
 *
 * @see PipelinePhase
 */
class Pipeline<S> {

	private final List<PipelinePhase> _phases;

	private final Map<PipelinePhase, List<PipelineInterceptor<S>>> interceptors = new HashMap<>();

	public Pipeline(PipelinePhase... phases) {
		_phases = new ArrayList<>(Arrays.asList(phases));
	}

	private PipelinePhase findPhase(final @NotNull PipelinePhase phase) {
		final List<PipelinePhase> phasesList = _phases;

		for (int i = 0; i < phasesList.size(); i++) {
			final PipelinePhase curr = phasesList.get(i);
			if (curr.equals(phase)) {
				phasesList.set(i, phase);
				return curr;
			}
		}

		return null;
	}

	private int findIndexOrThrow(final @NotNull PipelinePhase phase) {
		final List<PipelinePhase> phasesList = new ArrayList<>(_phases);
		for (int i = 0; i < phasesList.size(); i++) {
			final PipelinePhase curr = phasesList.get(i);
			if (curr.equals(phase))
				return i;
		}

		throw new IllegalArgumentException(String.format(
			"Phase %s was not registered for this pipeline",
			phase
		));
	}

	public boolean hasPhase(final @NotNull PipelinePhase phase) {
		final List<PipelinePhase> phasesList = new ArrayList<>(_phases);
		for (final PipelinePhase curr : phasesList) {
			if (curr.equals(phase))
				return true;
		}

		return false;
	}

	public void addPhase(final @NotNull PipelinePhase phase) {
		if (hasPhase(phase))
			return;

		_phases.add(phase);
	}

	public void insertPhaseBefore(
		final @NotNull PipelinePhase reference,
		final @NotNull PipelinePhase phase
	) {
		if (hasPhase(phase)) return;

		final int refIdx = findIndexOrThrow(reference);
		_phases.add(refIdx, phase);
	}

	public void insertPhaseAfter(
		final @NotNull PipelinePhase reference,
		final @NotNull PipelinePhase phase
	) {
		if (hasPhase(phase)) return;

		final int refIdx = findIndexOrThrow(reference);
		_phases.add(refIdx + 1, phase);
	}

	public void intercept(
		final @NotNull PipelinePhase phase,
		final @NotNull PipelineInterceptor<S> interceptor
	) {
		final PipelinePhase pipelinePhase = findPhase(phase);
		if (pipelinePhase == null)
			throw new IllegalArgumentException(String.format(
				"Phase %s was not registered for this pipeline",
				phase
			));

		interceptors.computeIfAbsent(phase, $ -> new ArrayList<>()).add(interceptor);
		afterIntercept();
	}

	public void afterIntercept() {
	}

	public PipelineContext<S> execute(
		@Nullable final S subject
	) {
		final PipelineContext<S> context = new PipelineContext<>(
			subject,
			interceptors.values().stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList()),
			0
		);
		context.execute(subject);
		return context;
	}

}
