package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

/**
 * It has subclasses that are intercepts of a view's lifecycle that determine whether an update job
 * should be started or stopped.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AutomaticUpdateInitiationInterceptor {

    /**
     * Intercepts the {@link AbstractView#RENDER} pipeline phase of the view and determines whether
     * the update job should be resumed.
     */
    @RequiredArgsConstructor
    static class Init implements PipelineInterceptor<ViewContext> {

        @Override
        public void intercept(@NotNull PipelineContext<ViewContext> pipeline, ViewContext subject) {
            if (subject.getUpdateJob() == null) return;

            // only init update job IN THE FIRST viewer
            if (subject.getViewers().size() != 1) return;

            subject.getUpdateJob().resume();
			calledSuccessfully();
        }

		@TestOnly
		void calledSuccessfully() {}
    }

    /**
     * Intercepts the {@link AbstractView#CLOSE} pipeline phase of the view and determines whether the update job should be
     * interrupted.
     */
    @RequiredArgsConstructor
    static class Interrupt implements PipelineInterceptor<ViewContext> {

        @Override
        public void intercept(@NotNull PipelineContext<ViewContext> pipeline, ViewContext subject) {
            if (subject.getUpdateJob() == null) return;

            // only pause update job if there's no more viewers in this context
            if (subject.getViewers().size() != 1) return;

            subject.getUpdateJob().pause();
			calledSuccessfully();
		}

		@TestOnly
		void calledSuccessfully() {}
    }
}
