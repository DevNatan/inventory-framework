package me.saiintbrisson.minecraft;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PipelineTest {

	private final PipelinePhase pipelinePhase = new PipelinePhase("Phase");

	@Test
	public void singleActionPipeline() {
		List<String> events = new ArrayList<>();
		Pipeline<String, Void> pipeline = new Pipeline<>();
		pipeline.execute(null, "abc");
		pipeline.intercept(pipelinePhase, ($, subject) -> events.add("intercepted " + subject));
		assertEquals(Collections.singletonList("intercepted abc"), events);
	}

}
