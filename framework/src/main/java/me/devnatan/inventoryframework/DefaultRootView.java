package me.devnatan.inventoryframework;

import static java.lang.String.format;
import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedMap;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.CLICK;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.CLOSE;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.INIT;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.INVALIDATION;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.OPEN;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.RENDER;
import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.UPDATE;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

class DefaultRootView implements RootView {

    private final UUID id = UUID.randomUUID();
    private ViewConfig config;
    private final Pipeline<? super VirtualView> pipeline =
            new Pipeline<>(INIT, OPEN, RENDER, UPDATE, CLICK, CLOSE, INVALIDATION);
    private final Set<IFContext> contexts = newSetFromMap(synchronizedMap(new HashMap<>()));

    @Override
    public final @NotNull UUID getUniqueId() {
        return id;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public final void renderItem(@NotNull IFContext context, @NotNull IFItem<?> item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void removeItem(@NotNull IFContext context, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return config;
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
    public final @UnmodifiableView List<Component> getComponents() {
        return null;
    }

    @Override
    public final @Nullable IFItem<?> getItem(int index) {
        return null;
    }

    @Override
    public void onInit(ViewConfigBuilder config) {}
}
