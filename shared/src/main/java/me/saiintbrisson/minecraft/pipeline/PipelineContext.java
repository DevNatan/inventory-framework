package me.saiintbrisson.minecraft.pipeline;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class PipelineContext<S> {

    private S subject;
    private final List<PipelineInterceptor<S>> interceptors;

    private int index;

	boolean isFinished() {
		return index == -1;
	}

    /** Finishes current pipeline execution */
    public void finish() {
        index = -1;
    }

    private void loop() {
        do {
            final int pointer = index;
            if (pointer == -1) break;

            final List<PipelineInterceptor<S>> safeInterceptors = interceptors;
            if (pointer >= safeInterceptors.size()) {
                finish();
                break;
            }

            final PipelineInterceptor<S> nextInterceptor = safeInterceptors.get(pointer);
            index = pointer + 1;

            nextInterceptor.intercept(this, subject);
        } while (true);
    }

    public void proceed() {
        if (index >= interceptors.size()) {
            finish();
            return;
        }

        loop();
    }

    public void execute(S initial) {
        index = 0;
        subject = initial;
        proceed();
    }
}
