package me.devnatan.inventoryframework;

import static java.lang.String.format;
import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedMap;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.CLICK;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.CLOSE;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.FIRST_RENDER;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.INIT;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.INVALIDATION;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.OPEN;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.UPDATE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.UnmodifiableView;
import org.jetbrains.annotations.VisibleForTesting;

@VisibleForTesting
public class DefaultRootView implements RootView {

    private final UUID id = UUID.randomUUID();
    private ViewConfig config;
    private final Pipeline<? super VirtualView> pipeline =
            new Pipeline<>(INIT, OPEN, FIRST_RENDER, UPDATE, CLICK, CLOSE, INVALIDATION);
    private final Set<IFContext> contexts = newSetFromMap(synchronizedMap(new HashMap<>()));

    // --- State Management --
    protected final StateFactory stateFactory = new StateFactory();
    private final List<State<?>> states = new ArrayList<>();

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
        return Collections.unmodifiableSet(contexts);
    }

    @Override
    public @NotNull IFContext getContext(@NotNull Viewer viewer) {
        for (final IFContext context : contexts) {
            if (context.getIndexedViewers().containsKey(viewer.getId())) return context;
        }

        throw new IllegalArgumentException(format("Unable to get context for %s", viewer));
    }

    @Override
    public @NotNull IFContext getContextByViewer(@NotNull String id) {
        for (final IFContext context : contexts) {
            if (context.getIndexedViewers().containsKey(id)) return context;
        }

        throw new IllegalArgumentException(format("Unable to get context for %s", id));
    }

    @Override
    public final void addContext(@NotNull IFContext context) {
        synchronized (contexts) {
            contexts.add(context);
        }
    }

    @Override
    public final void removeContext(@NotNull IFContext context) {
        synchronized (contexts) {
            contexts.add(context);
        }
    }

    @Override
    public final void renderContext(@NotNull IFContext context) {
        getPipeline().execute(FIRST_RENDER, context);
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(@NotNull ViewConfig config) {
        if (this.config != null) throw new IllegalStateException("Configuration was already set on initialization");

        this.config = config;
    }

    @Override
    public final @NotNull Pipeline<? super VirtualView> getPipeline() {
        return pipeline;
    }

    @Override
    public void open(@NotNull Viewer viewer) {
        throw new UnsupportedOperationException("Missing #open(...) implementation");
    }

    @Override
    public final void closeForEveryone() {
        getContexts().forEach(IFContext::close);
    }

    @Override
    public void onInit(ViewConfigBuilder config) {}

    @NotNull
    @Override
    public final Iterator<IFContext> iterator() {
        return getContexts().iterator();
    }

    @ApiStatus.Internal
    public @NotNull ElementFactory getElementFactory() {
        throw new UnsupportedOperationException("Element factory not provided");
    }
}
