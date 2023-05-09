package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;

public final class PlatformUpdateHandlerInterceptor implements PipelineInterceptor<VirtualView> {

	@SuppressWarnings("unchecked")
	@Override
	public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
		if (!(subject instanceof IFContext) || pipeline.getPhase() != StandardPipelinePhases.UPDATE)
			return;

		@SuppressWarnings("rawtypes") final PlatformView root = (PlatformView) ((IFContext) subject).getRoot();
		root.onUpdate((IFContext) subject);
	}
}
