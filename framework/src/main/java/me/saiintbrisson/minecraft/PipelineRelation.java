package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PipelineRelation {

	@Getter
	@RequiredArgsConstructor
	public static final class Before extends PipelineRelation {

		private final PipelinePhase relativeTo;

	}

	@Getter
	@RequiredArgsConstructor
	public static final class After extends PipelineRelation {

		private final PipelinePhase relativeTo;

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Last extends PipelineRelation {

		public static final PipelineRelation INSTANCE = new Last();

	}


}
