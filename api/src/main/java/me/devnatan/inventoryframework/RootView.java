package me.devnatan.inventoryframework;

import static me.devnatan.inventoryframework.ViewConfig.createOption;

import java.util.Set;
import me.devnatan.inventoryframework.ViewConfig.Option;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface RootView extends VirtualView {

    Option<Boolean> CancelOnClick = createOption("cancel-on-click", true);

    @NotNull
    @UnmodifiableView
    Set<IFContext> getContexts();

    /**
     * The configuration for this view.
     *
     * @return The configuration for this view.
     * @throws IllegalStateException If the configuration is not available yet (uninitialized)
     */
    @NotNull
    ViewConfig getConfig();

    /**
     * The execution pipeline for this view.
     *
     * @return The pipeline for this view.
     */
    @NotNull
    Pipeline<? super VirtualView> getPipeline();

    /**
     * Called when the view is about to be configured, the returned object will be the view's
     * configuration.
     * <p>
     * As a reference, the data defined here was defined in the constructor in previous versions.
     */
    @ApiStatus.OverrideOnly
    void onInit(ViewConfigBuilder config);
}
