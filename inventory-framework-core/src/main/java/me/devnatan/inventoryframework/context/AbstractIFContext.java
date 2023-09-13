package me.devnatan.inventoryframework.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.UnsupportedOperationInSharedContextException;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentComposition;
import me.devnatan.inventoryframework.component.ComponentContainer;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import me.devnatan.inventoryframework.state.DefaultStateValueHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

abstract class AbstractIFContext extends DefaultStateValueHost implements IFContext {

    private final List<Component> components = new LinkedList<>();
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
    public final @NotNull String getInitialTitle() {
        return getConfig().getTitle().toString();
    }

    @Override
    public @UnmodifiableView @NotNull List<Component> getComponents() {
        return Collections.unmodifiableList(getInternalComponents());
    }

    @Override
    public List<Component> getInternalComponents() {
        return components;
    }

    @Override
    public List<Component> getComponentsAt(int position) {
        final List<Component> componentList = new ArrayList<>();
        for (final Component component : getInternalComponents()) {
            if (component.isContainedWithin(position)) {
                componentList.add(component);
            }
        }
        return componentList;
    }

    @Override
    public void addComponent(@NotNull Component component) {
        synchronized (getInternalComponents()) {
            getInternalComponents().add(0, component);
        }
    }

    @Override
    public void removeComponent(@NotNull Component component) {
        synchronized (getInternalComponents()) {
            getInternalComponents().remove(component);
        }
    }

    private IFSlotRenderContext createSlotRenderContext(@NotNull Component component) {
        if (!(this instanceof IFRenderContext))
            throw new InventoryFrameworkException("Slot render context cannot be created from non-render parent");

        final IFRenderContext renderContext = (IFRenderContext) this;
        return getRoot()
                .getElementFactory()
                .createSlotRenderContext(component.getPosition(), renderContext, renderContext.getViewer());
    }

    int modCount = 0;

    @Override
    public void renderComponent(@NotNull Component component) {
        if (!component.shouldRender(this)) {
            component.setVisible(false);

            final Optional<Component> overlapOptional = getOverlappingComponentToRender(this, component);
            if (overlapOptional.isPresent()) {
                Component overlap = overlapOptional.get();

                // ComponentComposition can have a single child overlapping this component so,
                // to prevent re-render of the entire component we just seek for this single child
                // and render it individually
                //				while (overlap instanceof ComponentComposition) {
                //					final ComponentComposition composition = (ComponentComposition) overlap;
                //					overlap = getOverlappingComponent(composition, overlap).orElse(overlap);
                //				}

                renderComponent(overlap);

                if (overlap.isVisible()) return;
            }

            component.clear(this);
            return;
        }
        component.render(createSlotRenderContext(component));
    }

    private Optional<Component> getOverlappingComponentToRender(ComponentContainer container, Component subject) {
        // TODO Support recursive overlapping (more than two components overlapping each other)
        for (final Component child : container.getInternalComponents()) {
            if (!child.isVisible()) continue;
            if (child.getKey().equals(subject.getKey())) continue;
            if (child instanceof ComponentComposition) {
                // This prevents from child being compared with its own root that would cause an
                // infinite rendering loop causing the root being re-rendered entirely, thus the
                // child, because child always intersects with its root since it is inside it
                if (subject.getRoot() instanceof Component
                        && child.getKey().equals(((Component) subject.getRoot()).getKey())) {
                    continue;
                }

                // We skip ComponentComposition here because is expected to ComponentComposition,
                // on its render handler use #renderComponent to render its children so each
                // child will have its own overlapping checks
                for (final Component deepChild : ((ComponentComposition) child).getInternalComponents()) {
                    if (!deepChild.isVisible()) continue;
                    if (deepChild.intersects(subject)) return Optional.of(deepChild);
                }

                // Ignore ComponentComposition, we want to check intersections only with children
                continue;
            }

            if (child.intersects(subject)) return Optional.of(child);
        }

        return Optional.empty();
    }

    @Override
    public void updateComponent(@NotNull Component component) {
        component.updated(createSlotRenderContext(component));
    }

    @Override
    public void update() {
        getRoot().getPipeline().execute(StandardPipelinePhases.UPDATE, this);
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
                + getId() + ", viewers="
                + getIndexedViewers() + ", config="
                + getConfig() + ", initialData="
                + getInitialData() + "} "
                + super.toString();
    }
}
