package me.saiintbrisson.minecraft.pipeline.interceptors;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.IFContext;
import me.saiintbrisson.minecraft.internal.Job;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

/**
 * Intercepts view's open and close lifecycle that determine whether an update job should be started
 * or stopped. An "update job" is created when user schedules an update.
 *
 * @see me.saiintbrisson.minecraft.AbstractView#scheduleUpdate(Duration)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScheduledUpdateInterceptor {

    /**
     * Intercepts the render pipeline phase of the view and determines whether the update job should
     * be started/resumed.
     */
    @RequiredArgsConstructor
    public static class Render implements PipelineInterceptor<IFContext> {

        @Override
        public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext subject) {
            Job updateJob = subject.getRoot().getUpdateJob();

            if (updateJob == null || updateJob.isStarted()) return;

            // only init update job IN THE FIRST viewer
            if (subject.getViewers().size() != 1) return;

            updateJob.start();
            onIntercept();
        }

        @TestOnly
        void onIntercept() {}
    }

    /**
     * Intercepts the close pipeline phase of the view and determines whether the update job should
     * be interrupted.
     */
    @RequiredArgsConstructor
    public static class Close implements PipelineInterceptor<IFContext> {

        @Override
        public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext subject) {
            Job updateJob = subject.getRoot().getUpdateJob();
            if (updateJob == null || !updateJob.isStarted()) return;

            // only pause update job if there's no more viewers in this context
            if (subject.getViewers().size() != 1) return;

            updateJob.cancel();
            onIntercept();
        }

        @TestOnly
        void onIntercept() {}
    }
}
