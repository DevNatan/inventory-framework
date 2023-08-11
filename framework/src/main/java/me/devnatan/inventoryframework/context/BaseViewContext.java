package me.devnatan.inventoryframework.context;

import java.util.*;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import me.devnatan.inventoryframework.state.DefaultStateValueHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

class BaseViewContext extends DefaultStateValueHost implements IFContext {

    private final UUID id = UUID.randomUUID();
    private final @NotNull RootView root;
    // Container can be null on pre-render/intermediate contexts
    private final @Nullable ViewContainer container;
    private final List<Component> components = new LinkedList<>();
    private final Deque<Integer> markedForRemoval = new ArrayDeque<>();
    private final Object initialData;
    protected final Map<String, Viewer> viewers;
    protected ViewConfig config;

    public BaseViewContext(
            @NotNull RootView root,
            @Nullable ViewContainer container,
            @NotNull Map<String, Viewer> viewers,
            Object initialData) {
        this.root = root;
        this.container = container;
        this.config = root.getConfig();
        this.initialData = initialData;
        this.viewers = new HashMap<>(viewers);
    }

    @NotNull
    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public @NotNull ViewConfig getConfig() {
        return config;
    }

    @Override
    public final @NotNull RootView getRoot() {
        return root;
    }

    @Override
    public @NotNull ViewContainer getContainer() {
        if (container == null) throw new IllegalStateException("Unable to get null container");
        return container;
    }

    @Override
    public final @NotNull @Unmodifiable List<Viewer> getViewers() {
        return Collections.unmodifiableList(new ArrayList<>(getIndexedViewers().values()));
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Viewer> getIndexedViewers() {
        return Collections.unmodifiableMap(viewers);
    }

    @Override
    public final void addViewer(@NotNull Viewer viewer) {
        synchronized (viewers) {
            viewers.put(viewer.getId(), viewer);
        }
    }

    @Override
    public final void removeViewer(@NotNull Viewer viewer) {
        synchronized (viewers) {
            viewers.remove(viewer.getId());
        }
    }

    @Override
    public @NotNull String getTitle() {
        return getUpdatedTitle() == null ? getInitialTitle() : getUpdatedTitle();
    }

    @Override
    public final @NotNull String getInitialTitle() {
        return container.getTitle();
    }

    @Override
    public final @Nullable String getUpdatedTitle() {
        return getContainer().getTitle();
    }

    @Override
    public final void updateTitleForEveryone(@NotNull String title) {
        getContainer().changeTitle(title);
    }

    @Override
    public final void resetTitleForEveryone() {
        getContainer().changeTitle(null);
    }

    @Override
    public void closeForEveryone() {
        getContainer().close();
    }

    @Override
    public final void openForEveryone(Class<? extends RootView> other) {
        openForEveryone(other, null);
    }

    @Override
    public void openForEveryone(Class<? extends RootView> other, Object initialData) {
        getRoot().getFramework().open(other, getViewers(), initialData);
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

    @Override
    public void updateComponent(@NotNull Component component) {
        final Viewer subject = this instanceof IFConfinedContext ? ((IFConfinedContext) this).getViewer() : null;

        final IFSlotRenderContext context = getRoot()
                .getElementFactory()
                .createSlotContext(
                        component.getPosition(),
                        component,
                        getContainer(),
                        subject,
                        getIndexedViewers(),
                        this,
                        IFSlotRenderContext.class);

        component.updated(context);
    }

    @Override
    public void update() {
        getRoot().getPipeline().execute(StandardPipelinePhases.UPDATE, this);
    }

    @Override
    public boolean isMarkedForRemoval(int componentIndex) {
        return markedForRemoval.contains(componentIndex);
    }

    @Override
    public Object getInitialData() {
        return initialData instanceof Map ? Collections.unmodifiableMap((Map<?, ?>) initialData) : initialData;
    }

    @Override
    public boolean isShared() {
        return getViewers().size() > 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseViewContext that = (BaseViewContext) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "BaseViewContext{" + "id="
                + id + ", container="
                + container + ", viewers="
                + viewers + ", config="
                + config + ", markedForRemoval="
                + markedForRemoval + ", initialData="
                + initialData + "} "
                + super.toString();
    }
}
