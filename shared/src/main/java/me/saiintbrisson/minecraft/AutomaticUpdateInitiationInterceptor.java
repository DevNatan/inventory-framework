package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AutomaticUpdateInitiationInterceptor {

    public static final class Open implements PipelineInterceptor<OpenViewContext> {

        @Override
        public void intercept(@NotNull PipelineContext<OpenViewContext> pipeline, OpenViewContext subject) {
            if (subject.getUpdateJob() == null) return;

            // only init update job if there's more than zero viewers
            if (subject.getViewers().size() != 1) return;

            subject.getUpdateJob().resume();
        }
    }

    public static final class Close implements PipelineInterceptor<CloseViewContext> {

        @Override
        public void intercept(@NotNull PipelineContext<CloseViewContext> pipeline, CloseViewContext subject) {
            if (subject.getUpdateJob() == null) return;

            // only pause update job if there's no more viewers in this context
            if (subject.getViewers().size() != 1) return;

            subject.getUpdateJob().pause();
        }
    }
}
