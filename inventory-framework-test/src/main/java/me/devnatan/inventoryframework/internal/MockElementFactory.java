package me.devnatan.inventoryframework.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.devnatan.inventoryframework.*;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.context.*;
import me.devnatan.inventoryframework.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MockElementFactory extends ElementFactory {
    @Override
    public Logger getLogger() {
        return mock(Logger.class);
    }

    @Override
    public @NotNull RootView createUninitializedRoot() {
        return mock(RootView.class);
    }

    @Override
    public @NotNull ViewContainer createContainer(@NotNull IFContext context) {
        return mock(ViewContainer.class);
    }

    @Override
    public @NotNull Viewer createViewer(@NotNull Object entity, IFRenderContext context) {
        return mock(Viewer.class);
    }

    @Override
    public IFOpenContext createOpenContext(
            @NotNull RootView root, @Nullable Viewer subject, @NotNull List<Viewer> viewers, Object initialData) {
        IFOpenContext mock = mock(IFOpenContext.class);
        when(mock.getRoot()).thenReturn(root);
        when(mock.getViewer()).thenReturn(subject);
        when(mock.getViewers()).thenReturn(viewers);
        when(mock.getInitialData()).thenReturn(initialData);
        return mock;
    }

    @Override
    public IFRenderContext createRenderContext(
            @NotNull UUID id,
            @NotNull RootView root,
            @NotNull ViewConfig config,
            @NotNull ViewContainer container,
            @NotNull Map<String, Viewer> viewers,
            Viewer subject,
            Object initialData) {
        IFRenderContext mock = mock(IFRenderContext.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getRoot()).thenReturn(root);
        when(mock.getConfig()).thenReturn(config);
        when(mock.getContainer()).thenReturn(container);
        when(mock.getIndexedViewers()).thenReturn(viewers);
        when(mock.getViewer()).thenReturn(subject);
        when(mock.getInitialData()).thenReturn(initialData);
        return mock;
    }

    @Override
    public IFSlotClickContext createSlotClickContext(
            int slotClicked,
            @NotNull Viewer whoClicked,
            @NotNull ViewContainer interactionContainer,
            @Nullable Component componentClicked,
            @NotNull Object origin,
            boolean combined) {
        IFSlotClickContext mock = mock(IFSlotClickContext.class);
        when(mock.getSlot()).thenReturn(slotClicked);
        when(mock.getViewer()).thenReturn(whoClicked);
        when(mock.getContainer()).thenReturn(interactionContainer);
        when(mock.getComponent()).thenReturn(componentClicked);
        when(mock.getPlatformEvent()).thenReturn(origin);
        when(mock.isCombined()).thenReturn(combined);
        return mock;
    }

    @Override
    public IFSlotRenderContext createSlotRenderContext(
            int slot, @NotNull IFRenderContext parent, @Nullable Viewer viewer, Component component) {
        IFSlotRenderContext mock = mock(IFSlotRenderContext.class);
        when(mock.getSlot()).thenReturn(slot);
        when(mock.getParent()).thenReturn(parent);
        when(mock.getViewer()).thenReturn(viewer);
        when(mock.getContainer()).then(ignored -> parent.getContainer());
        when(mock.getComponent()).then(component);
        return mock;
    }

    @Override
    public IFCloseContext createCloseContext(
            @NotNull Viewer viewer, @NotNull IFRenderContext parent, @NotNull Object origin) {
        IFCloseContext mock = mock(IFCloseContext.class);
        when(mock.getViewer()).thenReturn(viewer);
        when(mock.getParent()).thenReturn(parent);
        when(mock.getPlatformEvent()).thenReturn(origin);
        return mock;
    }

    @Override
    public ComponentBuilder<?, ?> createComponentBuilder(@NotNull VirtualView root) {
        return mock(ComponentBuilder.class);
    }

    @Override
    public boolean worksInCurrentPlatform() {
        return true;
    }

    @Override
    public Job scheduleJobInterval(@NotNull RootView root, long intervalInTicks, @NotNull Runnable execution) {
        return null;
    }
}
