package me.devnatan.inventoryframework.context;

import lombok.Getter;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
class ConfinedContext extends BaseViewContext implements IFConfinedContext {

    private final Viewer viewer;

    public ConfinedContext(@NotNull RootView root, @Nullable ViewContainer container, @NotNull Viewer viewer) {
        super(root, container);
        this.viewer = viewer;
    }

    @Override
    public void closeForPlayer() {
        getContainer().close(viewer);
    }

    @Override
    public void openForPlayer(Class<? extends RootView> other) {
        getContainer().changeTitle(null, viewer);
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
