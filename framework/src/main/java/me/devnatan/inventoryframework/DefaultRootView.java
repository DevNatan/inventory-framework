package me.devnatan.inventoryframework;

import static java.lang.String.format;
import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedMap;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.CLICK;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.CLOSE;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.FIRST_RENDER;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.INIT;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.INVALIDATION;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.LAYOUT_RESOLUTION;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.OPEN;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.UPDATE;

import java.util.*;
import java.util.function.Consumer;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.Job;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.state.StateRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.UnmodifiableView;

@ApiStatus.NonExtendable
public class DefaultRootView implements RootView {

    private final UUID id = UUID.randomUUID();
    private ViewConfig config;
    private final Pipeline<VirtualView> pipeline =
            new Pipeline<>(INIT, OPEN, LAYOUT_RESOLUTION, FIRST_RENDER, UPDATE, CLICK, CLOSE, INVALIDATION);
    private final Set<IFContext> contexts = newSetFromMap(synchronizedMap(new HashMap<>()));
    final StateRegistry stateRegistry = new StateRegistry();
    private Job scheduledUpdateJob;
    private final Map<String, Object> metadata = new HashMap<>();

    @Override
    public final @NotNull UUID getUniqueId() {
        return id;
    }

    @TestOnly
    public final @NotNull Set<IFContext> getInternalContexts() {
        return contexts;
    }

    @Override
    public final @NotNull @UnmodifiableView Set<IFContext> getContexts() {
        return Collections.unmodifiableSet(getInternalContexts());
    }

    @Override
    public final @NotNull IFContext getContext(@NotNull Viewer viewer) {
        for (final IFContext context : getInternalContexts()) {
            if (context.getIndexedViewers().containsKey(viewer.getId())) return context;
        }

        throw new IllegalArgumentException(format("Unable to get context for %s", viewer));
    }

    @Override
    public final @NotNull IFContext getContext(@NotNull String viewerId) {
        for (final IFContext context : getInternalContexts()) {
            if (context.getIndexedViewers().containsKey(viewerId)) return context;
        }

        throw new IllegalArgumentException(format("Unable to get context for %s", viewerId));
    }

    @Override
    public final void addContext(@NotNull IFContext context) {
        synchronized (getInternalContexts()) {
            getInternalContexts().add(context);
        }
    }

    @Override
    public final void removeContext(@NotNull IFContext context) {
        synchronized (getInternalContexts()) {
            getInternalContexts().removeIf(other -> other.getId() == context.getId());
        }
    }

    @Override
    public final void renderContext(@NotNull IFContext context) {
        getPipeline().execute(context);
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

    @Override
    public void open(@NotNull List<Viewer> viewers, Object initialData) {
        throw new UnsupportedOperationException("Missing #open(...) implementation");
    }

    @Override
    public final void closeForEveryone() {
        getContexts().forEach(IFContext::closeForEveryone);
    }

    @Override
    public void onInit(ViewConfigBuilder config) {}

    @NotNull
    @Override
    public final Iterator<IFContext> iterator() {
        return getContexts().iterator();
    }

    @Override
    public final void forEach(Consumer<? super IFContext> action) {
        RootView.super.forEach(action);
    }

    @Override
    public final Spliterator<IFContext> spliterator() {
        return RootView.super.spliterator();
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
    public Job getScheduledUpdateJob() {
        return scheduledUpdateJob;
    }

    @Override
    public void setScheduledUpdateJob(@NotNull Job job) {
        this.scheduledUpdateJob = job;
    }

    @Override
    public IFViewFrame<?> getFramework() {
        throw new UnsupportedOperationException("Missing #getFramework() implementation");
    }

    @NotNull
    @Override
    public final Map<String, Object> getMetadata() {
        return metadata;
    }
}
