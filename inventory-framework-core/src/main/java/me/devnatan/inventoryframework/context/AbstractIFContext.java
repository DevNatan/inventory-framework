package me.devnatan.inventoryframework.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.UnsupportedOperationInSharedContextException;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import me.devnatan.inventoryframework.state.DefaultStateValueHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

abstract class AbstractIFContext extends DefaultStateValueHost implements IFContext {

    // TODO Reconsider usage of LinkedList - why its a LinkedList?? :clown:
    private final List<Component> components = new LinkedList<>();
    private final Map<String, Viewer> indexedViewers = new HashMap<>();
    protected ViewConfig config;

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
                if (component.isContainedWithin(position)) {
                    componentList.add(component);
                }
            }
        }
        return componentList;
    }

    @Override
    public final void addComponent(@NotNull Component component) {
		synchronized (getInternalComponents()) {
            getInternalComponents().add(0, component);
        }
    }

    @Override
    public final void removeComponent(@NotNull Component component) {
        synchronized (getInternalComponents()) {
            getInternalComponents().remove(component);
        }
    }

    @Override
    public void performClickInComponent(
            @NotNull Component component,
            @NotNull Viewer viewer,
            @NotNull ViewContainer clickedContainer,
            Object platformEvent,
            int clickedSlot,
            boolean combined) {
        final RootView root = (RootView) getRoot();
        final IFSlotClickContext clickContext = root.getElementFactory()
                .createSlotClickContext(clickedSlot, viewer, clickedContainer, component, platformEvent, combined);

        root.getPipeline().execute(StandardPipelinePhases.CLICK, clickContext);
    }
    // endregion

    @Override
    public void update() {
        ((RootView) getRoot()).getPipeline().execute(StandardPipelinePhases.UPDATE, this);
    }

    @Override
    public final boolean isShared() {
        return getIndexedViewers().size() > 1;
    }

    @NotNull
    @Override
    public final Iterator<Component> iterator() {
        return getComponents().iterator();
    }

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
