package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface RootView extends VirtualView {

	/**
	 * The execution pipeline for this view.
	 *
	 * @return The pipeline for this view.
	 */
	@NotNull
	Pipeline<IFContext> getPipeline();

	/**
	 * Called when the view is about to be configured, the returned object will be the view's
	 * configuration.
	 * <p>
	 * As a reference, the data defined here was defined in the constructor in previous versions.
	 */
	@ApiStatus.OverrideOnly
	void onInit(ViewConfigBuilder config);

}
