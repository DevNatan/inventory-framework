package me.devnatan.inventoryframework.context;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class ConfinedContext extends BaseViewContext implements IFConfinedContext {

    private final Viewer viewer;

    public ConfinedContext(@NotNull RootView root, @Nullable ViewContainer container, @NotNull Viewer viewer) {
        super(root, container);
        this.viewer = viewer;
    }

    @Override
    public @NotNull IFContext getParent() {
        return this;
    }

    @Override
    public void closeForPlayer() {
        getContainer().close(viewer);
    }

    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other) {
        getRoot().getFramework().open(other, getViewer());
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title) {
        getContainer().changeTitle(title, viewer);
    }

    @Override
    public void resetTitleForPlayer() {
        getContainer().changeTitle(null, viewer);
    }
}
