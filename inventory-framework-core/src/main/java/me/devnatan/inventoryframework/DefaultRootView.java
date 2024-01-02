package me.devnatan.inventoryframework;

import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.Job;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.state.StateRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public abstract class DefaultRootView implements RootView {

    private final UUID id = UUID.randomUUID();
    private ViewConfig config;
    private final Pipeline<RootView> pipeline = new Pipeline<>(PipelinePhase.View.values());
    private final Set<IFContext> contexts = newSetFromMap(synchronizedMap(new HashMap<>()));
    private final StateRegistry stateRegistry = new StateRegistry();
    private Job scheduledUpdateJob;

    Pipeline<RootView> getPipeline() {
        return pipeline;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void interceptPipelineCall(PipelinePhase phase, PipelineInterceptor<?> interceptor) {
        getPipeline().intercept(phase, (PipelineInterceptor<? extends RootView>) interceptor);
    }

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

    public StateRegistry getStateRegistry() {
        return stateRegistry;
    }
}
