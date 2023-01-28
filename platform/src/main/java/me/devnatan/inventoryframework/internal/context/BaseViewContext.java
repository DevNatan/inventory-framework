package me.devnatan.inventoryframework.internal.context;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.devnatan.inventoryframework.internal.state.DefaultStateHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@ApiStatus.Internal
public final class BaseViewContext extends DefaultStateHolder implements IFContext {

    private final @NotNull RootView root;
    private final @NotNull ViewContainer container;

    protected final Set<Viewer> viewers = new HashSet<>();

    private String updatedTitle;

    @Override
    public final @NotNull RootView getRoot() {
        return root;
    }

    @Override
    public final @NotNull ViewContainer getContainer() {
        return container;
    }

    @Override
    public final @NotNull Set<Viewer> getViewers() {
        return Collections.unmodifiableSet(viewers);
    }

    @Override
    public final @NotNull String getTitle() {
        return getUpdatedTitle() == null ? getInitialTitle() : getUpdatedTitle();
    }

    @Override
    public final @NotNull String getInitialTitle() {
        return container.getTitle();
    }

    @Override
    public final @Nullable String getUpdatedTitle() {
        return updatedTitle;
    }

    @Override
    public final void updateTitle(@NotNull String title) {
        this.updatedTitle = title;
        getContainer().changeTitle(title);
    }

    @Override
    public final void resetTitle() {
        this.updatedTitle = null;
        getContainer().changeTitle(null);
    }

    @Override
    public final void close() {
        getContainer().close();
    }

    @Override
    public final void open(Class<? extends RootView> other) {}
}
