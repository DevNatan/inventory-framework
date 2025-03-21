package me.devnatan.inventoryframework.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;

public abstract class PlatformConfinedContext extends PlatformContext implements IFConfinedContext {

    protected PlatformConfinedContext() {}

    @Override
    public abstract Viewer getViewer();

    @Override
    public void closeForPlayer() {
        getContainerOrThrow().close(getViewer());
    }

    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other) {
        openForPlayer(other, getConfig().isTransitiveInitialData() ? getInitialData() : null, false);
    }

    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData) {
        openForPlayer(other, initialData, true);
    }

    @SuppressWarnings("unchecked")
    private void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData, boolean mergeInitialData) {
        final Object data =
                getConfig().isTransitiveInitialData() && mergeInitialData ? mergeInitialData(initialData) : initialData;
        getRoot().navigateTo(other, (IFRenderContext) this, getViewer(), data);

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object mergeInitialData(Object otherData) {
        final Object initialData = getInitialData();
        if (initialData == null || otherData == null) return otherData == null ? initialData : otherData;

        if (!(initialData.getClass().equals(otherData.getClass())))
            throw new IllegalArgumentException(String.format(
                    "Failed to merge initial data, supplied data and current initial data must have the same type."
                            + " Initial data type is \"%s\" and supplied data type is \"%s\". Try changing type of both to a similar type like Map in order to merge be executed.",
                    initialData.getClass().getName(), otherData.getClass().getName()));

        final Object resultData;
        if (initialData instanceof List && otherData instanceof List) {
            final List<?> newData = new ArrayList<>();
            newData.addAll((List) initialData);
            newData.addAll((List) otherData);
            resultData = Collections.unmodifiableList(newData);
        } else if (initialData instanceof Map && otherData instanceof Map) {
            final Map<?, ?> newData = new HashMap<>();
            newData.putAll((Map) initialData);
            newData.putAll((Map) otherData);
            resultData = Collections.unmodifiableMap(newData);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unable to merge initial data (initial data type = %s, supplied data type = %s)",
                    initialData.getClass().getName(), otherData.getClass().getName()));
        }

        return resultData;
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
        back(null);
    }

    @Override
    public void back(Object initialData) {
        tryThrowDoNotWorkWithSharedContext();
        if (!canBack()) return;
        getRoot().back(getViewer(), initialData);
    }

    @Override
    public boolean canBack() {
        tryThrowDoNotWorkWithSharedContext();
        return getViewer().getPreviousContext() != null;
    }
}
