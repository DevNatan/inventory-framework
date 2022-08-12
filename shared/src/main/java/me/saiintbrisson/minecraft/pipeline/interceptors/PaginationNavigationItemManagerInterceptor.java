package me.saiintbrisson.minecraft.pipeline.interceptors;

import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

/**
 * Called during post-resolution initialization of a view's layout to predetermine the position of
 * navigation items in a paginated view.
 */
public final class PaginationNavigationItemManagerInterceptor implements PipelineInterceptor<VirtualView> {

	@Override
	public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView subject) {
		if (!subject.isPaginated())
			return;


	}

}
