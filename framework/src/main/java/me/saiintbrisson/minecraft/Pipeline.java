package me.saiintbrisson.minecraft;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
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
class Pipeline<S, C> {

	private final List<PipelinePhase> _phases;

	private final Map<PipelinePhase, List<PipelineInterceptor<S, C>>> interceptors = new HashMap<>();

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

	private int findIndex(final @NotNull PipelinePhase phase) {
		final List<PipelinePhase> phasesList = new ArrayList<>(_phases);
		for (int i = 0; i < phasesList.size(); i++) {
			final PipelinePhase curr = phasesList.get(i);
			if (curr.equals(phase))
				return i;
		}

		return -1;
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

		final int refIdx = findIndex(reference);
		if (refIdx == -1)
			throw new IllegalArgumentException(String.format(
				"Phase %s was not registered for this pipeline",
				reference
			));

		_phases.add(refIdx, phase);
	}

	public void insertPhaseAfter(
		final @NotNull PipelinePhase reference,
		final @NotNull PipelinePhase phase
	) {
		if (hasPhase(phase)) return;

		final int refIdx = findIndex(reference);
		if (refIdx == -1)
			throw new IllegalArgumentException(String.format(
				"Phase %s was not registered for this pipeline",
				reference
			));

		_phases.add(refIdx + 1, phase);
	}

	public void intercept(
		final @NotNull PipelinePhase phase,
		final @NotNull PipelineInterceptor<S, C> interceptor
	) {
		final PipelinePhase pipelinePhase = findPhase(phase);
		if (pipelinePhase == null)
			throw new IllegalArgumentException(String.format(
				"Phase %s was not registered for this pipeline",
				phase
			));

		final int lastIndex = _phases.size() - 1;
		if (phase.equals(_phases.get(lastIndex)) || findIndex(phase) == lastIndex) {
			final PipelinePhase target = findPhase(phase);
			if (target != null)
				target.add
			return true;
		}

		afterIntercept();
	}

	public void afterIntercept() {
	}

	public S execute(
		final @Nullable C context,
		final @NotNull S subject
	) {
		return new PipelineContext<>(context, subject, new ArrayList<>(interceptors.values().stream().flatMap(List::addAll).collect(Collectors.toList())), 0).execute(subject);
	}

}
