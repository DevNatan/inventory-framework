package me.devnatan.inventoryframework.context;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ConfinedContext extends BaseViewContext implements IFConfinedContext {

    private final Viewer viewer;

    public ConfinedContext(
            @NotNull RootView root,
            @Nullable ViewContainer container,
            Viewer subject,
            @NotNull Map<String, Viewer> viewers,
            Object initialData) {
        super(root, container, viewers, initialData);
        this.viewer = subject;
    }

    @NotNull
    @Override
    public Viewer getViewer() {
        return viewer;
    }

    @Override
    public void closeForPlayer() {
        getContainer().close(viewer);
    }

    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other) {
        openForPlayer(other, null);
    }

    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData) {
        getRoot().getFramework().open(other, Collections.singleton(getViewer()), initialData);
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title) {
        getContainer().changeTitle(title, getViewer());
    }

    @Override
    public void resetTitleForPlayer() {
        getContainer().changeTitle(null, getViewer());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfinedContext that = (ConfinedContext) o;
        return Objects.equals(getViewer(), that.getViewer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getViewer());
    }

    @Override
    public String toString() {
        return "ConfinedContext{" + "viewer=" + viewer + "} " + super.toString();
    }
}
