package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public final class BukkitMoveInInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(
		@NotNull final PipelineContext<BukkitClickViewSlotContext> pipeline,
		final BukkitClickViewSlotContext subject
	) {
		throw new UnsupportedOperationException("Move in is not yet supported");
	}

}
