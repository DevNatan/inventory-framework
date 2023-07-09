package me.devnatan.inventoryframework.context;

import java.util.Objects;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ConfinedContext extends BaseViewContext implements IFConfinedContext {

    private final Viewer viewer;

    public ConfinedContext(
            @NotNull RootView root, @Nullable ViewContainer container, @NotNull Viewer viewer, Object initialData) {
        super(root, container, initialData);
        this.viewer = viewer;
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
        getRoot().getFramework().open(other, getViewer(), getInitialData());
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title) {
        getContainer().changeTitle(title, viewer);
    }

    @Override
    public void resetTitleForPlayer() {
        getContainer().changeTitle(null, viewer);
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
