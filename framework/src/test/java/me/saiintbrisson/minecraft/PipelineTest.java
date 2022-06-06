package me.saiintbrisson.minecraft;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PipelineTest {

	private final PipelinePhase pipelinePhase = new PipelinePhase("Phase");

	@Test
	public void singleActionPipeline() {
		List<String> events = new ArrayList<>();
		Pipeline<String> pipeline = new Pipeline<>(pipelinePhase);
		pipeline.intercept(pipelinePhase, ($, subject) -> {
			events.add("intercepted " + subject);
		});
		pipeline.execute("some");
		assertEquals(Collections.singletonList("intercepted some"), events);
	}

	@Test
	public void singleActionPipelineWithFail() {
		List<String> events = new ArrayList<>();
		Pipeline<String> pipeline = new Pipeline<>(pipelinePhase);
		pipeline.intercept(pipelinePhase, ($, subject) -> {
			try {
				events.add("intercepted " + subject);
				throw new UnsupportedOperationException();
			} catch (final Throwable ignored) {
				events.add("fail " + subject);
			}
		});
		pipeline.execute("some");
		assertEquals(Arrays.asList("intercepted some", "fail some"), events);
	}

	@Test
	public void implicitProceed() {
		List<String> events = new ArrayList<>();
		Pipeline<String> pipeline = new Pipeline<>(pipelinePhase);
		pipeline.intercept(pipelinePhase, ($, subject) -> events.add("intercept1 " + subject));
		pipeline.intercept(pipelinePhase, ($, subject) -> events.add("intercept2 " + subject));
		pipeline.execute("some");
		assertEquals(Arrays.asList("intercept1 some", "intercept2 some"), events);
	}

	@Test
	public void actionFinishOrder() {
		List<String> events = new ArrayList<>();
		Pipeline<String> pipeline = new Pipeline<>(pipelinePhase);
		pipeline.intercept(pipelinePhase, (context, subject) -> {
			try {
				events.add("intercept1 " + subject);
				context.proceed();
				events.add("success1 " + subject);
			} catch (Throwable ignored) {
				events.add("fail1 " + subject);
			}
		});
		pipeline.intercept(pipelinePhase, (context, subject) -> {
			try {
				events.add("intercept2 " + subject);
				context.proceed();
				events.add("success2 " + subject);
			} catch (Throwable ignored) {
				events.add("fail2 " + subject);
			}
		});
		pipeline.execute("some");
		assertEquals(Arrays.asList(
			"intercept1 some",
			"intercept2 some",
			"success2 some",
			"success1 some"
		), events);
	}

	@Test
	public void actionFailOrder() {
		List<String> events = new ArrayList<>();
		Pipeline<String> pipeline = new Pipeline<>(pipelinePhase);
		pipeline.intercept(pipelinePhase, (context, subject) -> {
			try {
				events.add("intercept1 " + subject);
				context.proceed();
				events.add("success1 " + subject);
			} catch (Throwable ignored) {
				events.add("fail1 " + subject);
			}
		});
		pipeline.intercept(pipelinePhase, (context, subject) -> {
			try {
				events.add("intercept2 " + subject);
				throw new UnsupportedOperationException();
			} catch (Throwable e) {
				events.add("fail2 " + subject);
				throw e;
			}
		});
		pipeline.execute("some");
		assertEquals(Arrays.asList(
			"intercept1 some",
			"intercept2 some",
			"fail2 some",
			"fail1 some"
		), events);
	}

	@Test
	public void actionFinishAllOrder() {
		List<String> events = new ArrayList<>();
		Pipeline<String> p1 = new Pipeline<>(pipelinePhase);
		p1.intercept(pipelinePhase, (context, subject) -> {
			try {
				events.add("intercept-p1-1 " + subject);

				Pipeline<String> p2 = new Pipeline<>(pipelinePhase);
				p2.intercept(pipelinePhase, (context1, nested) -> {
					events.add("intercept-p2-1 " + nested);

					Pipeline<String> p3 = new Pipeline<>(pipelinePhase);
					p3.intercept(pipelinePhase, (context2, nested2) -> {
						events.add("intercept-p3-1 " + nested2);
						context2.proceed();
					});

					p3.intercept(pipelinePhase, (context2, nested2) -> {
						events.add("intercept-p3-2 " + nested2);
						context2.proceed();
					});

					p3.execute("p3");
					context1.proceed();
					events.add("success-p2-1 " + nested);
				});

				p2.execute("p2");
				context.proceed();
				events.add("success-p1-1 " + subject);
			} catch (Throwable e) {
				events.add("fail-p1-1 " + subject);
				throw e;
			}
		});

		p1.intercept(pipelinePhase, (context, subject) -> {
			events.add("intercept-p1-2 " + subject);
			context.proceed();
		});

		p1.execute("p1");
		assertEquals(Arrays.asList(
			"intercept-p1-1 p1",
			"intercept-p2-1 p2",
			"intercept-p3-1 p3",
			"intercept-p3-2 p3",
			"success-p2-1 p2",
			"intercept-p1-2 p1",
			"success-p1-1 p1"
		), events);
	}

	@Test
	public void phased() {
		PipelinePhase before = new PipelinePhase("before");
		PipelinePhase after = new PipelinePhase("after");
		Pipeline<String> pipeline = new Pipeline<>(before, after);

		checkPipelineOrder(after, before, pipeline);
	}

	@Test
	public void phasedNotRegistered() {
		PipelinePhase before = new PipelinePhase("before");
		PipelinePhase after = new PipelinePhase("after");
		Pipeline<String> pipeline = new Pipeline<>(before);

		assertThrows(IllegalArgumentException.class, () -> pipeline.intercept(after, ($, $$) -> {}));
	}

	@Test
	public void phasedBefore() {
		Pipeline<String> pipeline = new Pipeline<>();
		PipelinePhase before = new PipelinePhase("before");
		PipelinePhase after = new PipelinePhase("after");
		pipeline.addPhase(after);
		pipeline.insertPhaseBefore(after, before);
		checkPipelineOrder(after, before, pipeline);
	}

	@Test
	public void phasedAfter() {
		Pipeline<String> pipeline = new Pipeline<>();
		PipelinePhase before = new PipelinePhase("before");
		PipelinePhase after = new PipelinePhase("after");
		pipeline.addPhase(before);
		pipeline.insertPhaseAfter(before, after);
		checkPipelineOrder(after, before, pipeline);
	}

	private void checkPipelineOrder(
		PipelinePhase after,
		PipelinePhase before,
		Pipeline<String> pipeline
	) {
		ThreadLocal<Boolean> value = ThreadLocal.withInitial(() -> false);

		pipeline.intercept(after, (context, $) -> {
			value.set(true);
			context.proceed();
		});

		pipeline.intercept(before, (context, $) -> {
			assertFalse(value.get());
			context.proceed();
		});

		pipeline.execute("some");
		assertTrue(value.get());
	}

}