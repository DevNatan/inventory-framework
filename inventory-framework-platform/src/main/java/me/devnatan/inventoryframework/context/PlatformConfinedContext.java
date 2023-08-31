package me.devnatan.inventoryframework.context;

import java.util.Collections;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;

abstract class PlatformConfinedContext extends PlatformContext implements IFConfinedContext {

    @Override
    public abstract Viewer getViewer();

    @Override
    public void closeForPlayer() {
        getContainer().close(getViewer());
    }

    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other) {
        openForPlayer(other, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData) {
        getRoot()
                .getFramework()
                .getRegisteredViewByType(other)
                .open(Collections.singletonList(getViewer()), initialData);
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title) {
        getContainer().changeTitle(title, getViewer());
    }

    @Override
    public void resetTitleForPlayer() {
        getContainer().changeTitle(null, getViewer());
    }
}
