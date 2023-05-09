package me.devnatan.inventoryframework.context;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import me.devnatan.inventoryframework.state.DefaultStateValueHost;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

@ApiStatus.Internal
@ApiStatus.NonExtendable
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BaseViewContext extends DefaultStateValueHost implements IFContext {

    @Getter
    @EqualsAndHashCode.Include
    private final UUID id = UUID.randomUUID();

    @ToString.Exclude
    private final @NotNull RootView root;

    /* container can be null on pre-render/intermediate contexts */
    private final @Nullable ViewContainer container;

    protected final Map<String, Viewer> viewers = new HashMap<>();
    protected ViewConfig config;

    @ToString.Exclude
    private final List<Component> components = new LinkedList<>();

    private final Deque<Integer> markedForRemoval = new ArrayDeque<>();

    public BaseViewContext(@NotNull RootView root, @Nullable ViewContainer container) {
        this.root = root;
        this.container = container;
        this.config = root.getConfig();
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
    public final @NotNull @Unmodifiable Set<Viewer> getViewers() {
        return Collections.unmodifiableSet(new HashSet<>(getIndexedViewers().values()));
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
    public final void updateTitle(@NotNull String title) {
        getContainer().changeTitle(title);
    }

    @Override
    public final void resetTitle() {
        getContainer().changeTitle(null);
    }

    @Override
    public void closeForEveryone() {
        getContainer().close();
    }

    @Override
    public final void openForEveryone(Class<? extends RootView> other) {
        System.out.println("getViewers() = " + getViewers());
        getViewers().forEach(viewer -> getRoot().getFramework().open(other, viewer));
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
    public Pagination pagination() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void update() {
        getRoot().getPipeline().execute(StandardPipelinePhases.UPDATE, this);
    }

    @Override
    public boolean isMarkedForRemoval(int componentIndex) {
        return markedForRemoval.contains(componentIndex);
    }
}
