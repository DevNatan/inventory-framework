package me.devnatan.inventoryframework.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.UnsupportedOperationInSharedContextException;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.state.DefaultStateValueHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

abstract class AbstractIFContext extends DefaultStateValueHost implements IFContext {

    // TODO Reconsider usage of LinkedList - why its a LinkedList?? :clown:
    private final List<Component> components = new LinkedList<>();
    private final Map<String, Viewer> indexedViewers = new HashMap<>();
    protected ViewConfig config;
    private final Pipeline<IFContext> pipeline = new Pipeline<>(PipelinePhase.Context.values());

    Pipeline<IFContext> getPipeline() {
        return pipeline;
    }

    @Override
    public void update() {
        getPipeline().execute(PipelinePhase.Context.CONTEXT_UPDATE, this);
    }

    @Override
    public final boolean isShared() {
        return getIndexedViewers().size() > 1;
    }

    @Override
    public final @NotNull String getInitialTitle() {
        return getConfig().getTitle().toString();
    }

    // region Viewers API
    @Override
    public @NotNull Map<String, Viewer> getIndexedViewers() {
        return indexedViewers;
    }

    @Override
    public final @NotNull List<Viewer> getViewers() {
        return new ArrayList<>(getIndexedViewers().values());
    }

    @Override
    public final void addViewer(@NotNull Viewer viewer) {
        synchronized (getIndexedViewers()) {
            getIndexedViewers().put(viewer.getId(), viewer);
        }

        IFDebug.debug(
                "Viewer %s added to %s", viewer.getId(), getRoot().getClass().getName());
    }

    @Override
    public final void removeViewer(@NotNull Viewer viewer) {
        synchronized (getIndexedViewers()) {
            getIndexedViewers().remove(viewer.getId());
        }
        IFDebug.debug(
                "Viewer %s removed from %s",
                viewer.getId(), getRoot().getClass().getName());
    }
    // endregion

    // region Components API
    @Override
    public final @UnmodifiableView @NotNull List<Component> getComponents() {
        return Collections.unmodifiableList(getInternalComponents());
    }

    @Override
    public final List<Component> getInternalComponents() {
        return components;
    }

    @Override
    public final List<Component> getComponentsAt(int position) {
        final List<Component> componentList = new ArrayList<>();
        synchronized (getInternalComponents()) {
            for (final Component component : getInternalComponents()) {
                if (component.getHandle().isContainedWithin(position)) {
                    componentList.add(component);
                }
            }
        }
        return componentList;
    }
    // endregion

    /**
     * Throws a {@link InventoryFrameworkException} saying that the method that's being executed is
     * not supported if this context is a shared context.
     */
    final void tryThrowDoNotWorkWithSharedContext() {
        if (!isShared()) return;
        throw new UnsupportedOperationInSharedContextException();
    }

    /**
     * Throws a {@link InventoryFrameworkException} saying that the method that's being executed is
     * not supported if this context is a shared context, with a replacement message.
     *
     * @param replacement The alternative method to be used.
     */
    final void tryThrowDoNotWorkWithSharedContext(String replacement) {
        if (!isShared()) return;
        throw new UnsupportedOperationInSharedContextException(replacement);
    }

    @Override
    public String toString() {
        return "AbstractIFContext{" + "id="
                + getId() + ", indexedViewers="
                + getIndexedViewers() + ", config="
                + getConfig() + ", initialData="
                + getInitialData() + "} "
                + super.toString();
    }
}
