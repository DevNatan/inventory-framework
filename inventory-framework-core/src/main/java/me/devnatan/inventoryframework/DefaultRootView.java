package me.devnatan.inventoryframework;

import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedMap;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.*;

import java.util.*;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.Job;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.state.StateRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public abstract class DefaultRootView implements RootView {

    private final UUID id = UUID.randomUUID();
    private ViewConfig config;
    private final Pipeline<VirtualView> pipeline = new Pipeline<>(
            INIT,
            OPEN,
            VIEWER_ADDED,
            LAYOUT_RESOLUTION,
            FIRST_RENDER,
            UPDATE,
            CLICK,
            VIEWER_REMOVED,
            CLOSE,
            INVALIDATION);
    private final Set<IFContext> contexts = newSetFromMap(synchronizedMap(new HashMap<>()));
    final StateRegistry stateRegistry = new StateRegistry();
    private Job scheduledUpdateJob;

    @Override
    public final @NotNull UUID getUniqueId() {
        return id;
    }

    @Override
    public final Set<IFContext> getInternalContexts() {
        return contexts;
    }

    protected final Set<IFContext> getContexts() {
        return Collections.unmodifiableSet(getInternalContexts());
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return config;
    }

    @Override
    public final void setConfig(@NotNull ViewConfig config) {
        if (this.config != null) throw new IllegalStateException("Configuration was already set on initialization");

        this.config = config;
    }

    @Override
    public final @NotNull Pipeline<VirtualView> getPipeline() {
        return pipeline;
    }

    @ApiStatus.Internal
    public @NotNull ElementFactory getElementFactory() {
        throw new UnsupportedOperationException("Element factory not provided");
    }

    @Override
    public void nextTick(Runnable task) {
        throw new UnsupportedOperationException("Missing nextTick(...) implementation");
    }

    @Override
    public final Job getScheduledUpdateJob() {
        return scheduledUpdateJob;
    }

    @Override
    public final void setScheduledUpdateJob(@NotNull Job job) {
        this.scheduledUpdateJob = job;
    }
}
