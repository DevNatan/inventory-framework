package me.devnatan.inventoryframework.pipeline;

import java.util.List;
import me.devnatan.inventoryframework.IFDebug;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

@VisibleForTesting
public class PipelineContext<S> {

    private final PipelinePhase phase;
    private final List<PipelineInterceptor<S>> interceptors;
    private S subject;
    private int index;

    PipelineContext(PipelinePhase phase, List<PipelineInterceptor<S>> interceptors) {
        this.phase = phase;
        this.interceptors = interceptors;
    }

    public @Nullable PipelinePhase getPhase() {
        return phase;
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

            IFDebug.debug(
                    "Interception loop #%d: %s",
                    index, nextInterceptor.getClass().getName());
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
