package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;

abstract class PlatformConfinedContext extends PlatformContext implements IFConfinedContext {

    @Override
    public abstract Viewer getViewer();

    @Override
    public void closeForPlayer() {
        getContainerOrThrow().close(getViewer());
    }

    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other) {
        openForPlayer(other, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData) {
        getRoot().navigateTo(other, (IFRenderContext) this, getViewer(), initialData);
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title) {
        getContainerOrThrow().changeTitle(title, getViewer());
    }

    @Override
    public void resetTitleForPlayer() {
        getContainerOrThrow().changeTitle(null, getViewer());
    }

    @Override
    public void back() {
        tryThrowDoNotWorkWithSharedContext();
        if (!canBack()) return;
        getRoot().back(getViewer());
    }

    @Override
    public boolean canBack() {
        tryThrowDoNotWorkWithSharedContext();
        return getViewer().getPreviousContext() != null;
    }
}
