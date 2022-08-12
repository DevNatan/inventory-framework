package me.saiintbrisson.minecraft.pipeline.interceptors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.time.Duration;

/**
 * Intercepts view's open and close lifecycle that determine whether an update job should be started
 * or stopped. An "update job" is created when user {@link me.saiintbrisson.minecraft.VirtualView#scheduleUpdate(Duration) schedules a update}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScheduledUpdateInterceptor {

	/**
	 * Intercepts the render pipeline phase of the view and determines whether the update job should
	 * be started/resumed.
	 */
	@RequiredArgsConstructor
	public static class Render implements PipelineInterceptor<ViewContext> {

		@Override
		public void intercept(@NotNull PipelineContext<ViewContext> pipeline, ViewContext subject) {
			if (subject.getUpdateJob() == null) return;

			// only init update job IN THE FIRST viewer
			if (subject.getViewers().size() != 1) return;

			subject.getUpdateJob().resume();
			onIntercept();
		}

		@TestOnly
		void onIntercept() {
		}
	}

	/**
	 * Intercepts the close pipeline phase of the view and determines whether the update job should
	 * be interrupted.
	 */
	@RequiredArgsConstructor
	public static class Close implements PipelineInterceptor<ViewContext> {

		@Override
		public void intercept(@NotNull PipelineContext<ViewContext> pipeline, ViewContext subject) {
			if (subject.getUpdateJob() == null) return;

			// only pause update job if there's no more viewers in this context
			if (subject.getViewers().size() != 1) return;

			subject.getUpdateJob().pause();
			onIntercept();
		}

		@TestOnly
		void onIntercept() {
		}
	}
}
