package me.devnatan.inventoryframework;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface RootView extends VirtualView, Iterable<IFContext> {

    @NotNull
    UUID getUniqueId();

    @NotNull
    @UnmodifiableView
    Set<IFContext> getContexts();

    @NotNull
    IFContext getContext(@NotNull Viewer viewer);

    @NotNull
    IFContext getContextByViewer(@NotNull String id);

    void addContext(@NotNull IFContext context);

    void removeContext(@NotNull IFContext context);

    void renderContext(@NotNull IFContext context);

    /**
     * Called when the view is about to be configured, the returned object will be the view's
     * configuration.
     * <p>
     * As a reference, the data defined here was defined in the constructor in previous versions.
     *
     * @param config A mutable ViewConfigBuilder to configure this view.
     */
    @ApiStatus.OverrideOnly
    void onInit(ViewConfigBuilder config);

    /**
     * The configuration for this view.
     *
     * @return The configuration for this view.
     * @throws IllegalStateException If the configuration is not available yet (uninitialized)
     */
    @NotNull
    ViewConfig getConfig();

    /**
     * Sets the configuration for this view.
     *
     * @param config The new configuration of this view.
     * @throws IllegalStateException If the configuration was already set before.
     */
    void setConfig(@NotNull ViewConfig config);

    /**
     * The execution pipeline for this view.
     *
     * @return The pipeline for this view.
     */
    @NotNull
    Pipeline<VirtualView> getPipeline();

    void open(@NotNull Viewer viewer, @NotNull Map<String, Object> initialData);

    void closeForEveryone();

    @NotNull
    ElementFactory getElementFactory();

    void nextTick(Runnable task);
}
