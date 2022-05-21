package me.saiintbrisson.minecraft;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public final class PipelineContext<S, C> {

	private final C context;
	private S subject;
	private final List<PipelineInterceptor<S, C>> interceptors;

	private int index;

	/**
	 * Finishes current pipeline execution
	 */
	public void finish() {
		index = -1;
	}

	private S loop() {
		do {
			final int pointer = index;
			if (pointer == -1)
				break;

			final List<PipelineInterceptor<S, C>> safeInterceptors = interceptors;
			if (pointer >= safeInterceptors.size()) {
				finish();
				break;
			}

			final PipelineInterceptor<S, C> nextInterceptor = safeInterceptors.get(pointer);
			index = pointer + 1;

			nextInterceptor.intercept(this, subject);
		} while (true);

		return subject;
	}

	public S proceed() {
		if (index >= interceptors.size()) {
			finish();
			return subject;
		}

		return loop();
	}

	public S execute(S initial) {
		index = 0;
		subject = initial;
		return proceed();
	}

}
