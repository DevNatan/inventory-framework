package me.devnatan.inventoryframework.context;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.UnsupportedOperationInSharedContextException;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import me.devnatan.inventoryframework.state.DefaultStateValueHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

abstract class AbstractIFContext extends DefaultStateValueHost implements IFContext {

    private final List<Component> components = new LinkedList<>();
    private final Deque<Integer> markedForRemoval = new ArrayDeque<>();
    private final Map<String, Viewer> indexedViewers = new HashMap<>();
    protected ViewConfig config;

    @Override
    public @NotNull Map<String, Viewer> getIndexedViewers() {
        return indexedViewers;
    }

    @Override
    public final @NotNull @Unmodifiable List<Viewer> getViewers() {
        return Collections.unmodifiableList(new ArrayList<>(getIndexedViewers().values()));
    }

    @Override
    public final void addViewer(@NotNull Viewer viewer) {
        synchronized (getIndexedViewers()) {
            getIndexedViewers().put(viewer.getId(), viewer);
        }
    }

    @Override
    public final void removeViewer(@NotNull Viewer viewer) {
        synchronized (getIndexedViewers()) {
            getIndexedViewers().remove(viewer.getId());
        }
    }

    @Override
    public @NotNull String getTitle() {
        return getUpdatedTitle() == null ? getInitialTitle() : getUpdatedTitle();
    }

    @Override
    public final @NotNull String getInitialTitle() {
        return getConfig().getTitle().toString();
    }

    @Override
    public final @Nullable String getUpdatedTitle() {
        return getContainer().getTitle();
    }

    @Override
    public final void updateTitleForEveryone(@NotNull String title) {
        for (final Viewer viewer : getViewers()) getContainer().changeTitle(title, viewer);
    }

    @Override
    public final void resetTitleForEveryone() {
        for (final Viewer viewer : getViewers()) getContainer().changeTitle(null, viewer);
    }

    @Override
    public final void closeForEveryone() {
        getContainer().close();
    }

    @Override
    public final void openForEveryone(Class<? extends RootView> other) {
        openForEveryone(other, null);
    }

    @Override
    public @UnmodifiableView @NotNull List<Component> getComponents() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public Component getComponent(int position) {
        for (final Component component : getComponents()) {
            if (component.isContainedWithin(position)) return component;
        }
        return null;
    }

    @Override
    public void addComponent(@NotNull Component component) {
        synchronized (components) {
            components.add(0, component);
        }
    }

    @Override
    public void removeComponent(@NotNull Component component) {
        synchronized (components) {
            components.remove(component);
        }
    }

    private IFSlotRenderContext createRenderContext(@NotNull Component component) {
        if (!(this instanceof IFRenderContext))
            throw new InventoryFrameworkException("Slot render context cannot be created from non-render parent");

        final IFRenderContext renderContext = (IFRenderContext) this;
        return getRoot()
                .getElementFactory()
                .createSlotRenderContext(component.getPosition(), renderContext, renderContext.getViewer());
    }

    @Override
    public void renderComponent(@NotNull Component component) {
        if (!component.shouldRender(this)) {
            component.setVisible(false);

            // TODO Support recursive overlapping (more than two components overlapping each other)
            final Optional<Component> overlapOptional = getComponents().stream()
                    // FIXME This is kinda false positive needs a better explanation
                    .filter(Component::isVisible)
                    .filter(other -> other.intersects(component))
                    .findFirst();

            if (overlapOptional.isPresent()) {
                final Component overlap = overlapOptional.get();
                renderComponent(overlap);

                if (overlap.isVisible()) return;
            }

            component.clear(this);
            return;
        }

        component.render(createRenderContext(component));
    }

    @Override
    public void updateComponent(@NotNull Component component) {
        component.updated(createRenderContext(component));
    }

    @Override
    public void update() {
        getRoot().getPipeline().execute(StandardPipelinePhases.UPDATE, this);
    }

    @Override
    public final boolean isMarkedForRemoval(int componentIndex) {
        return markedForRemoval.contains(componentIndex);
    }

    @Override
    public final boolean isShared() {
        return getIndexedViewers().size() > 1;
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
                + getId() + ", container="
                + getContainer() + ", viewers="
                + getIndexedViewers() + ", config="
                + getConfig() + ", initialData="
                + getInitialData() + "} "
                + super.toString();
    }
}
