package me.devnatan.inventoryframework.bukkit.listener;

import static me.devnatan.inventoryframework.TestUtils.createContextMock;
import static me.devnatan.inventoryframework.TestUtils.createRootMock;
import static me.devnatan.inventoryframework.ViewConfig.CANCEL_ON_DROP;
import static me.devnatan.inventoryframework.ViewConfig.CANCEL_ON_PICKUP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.IFContext;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.junit.jupiter.api.Test;

public class IFInventoryListenerTest {

    @Test
    void shouldCancelItemDrop() {
        RootView root = createRootMock();
        PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
        ViewFrame viewFrame = mock(ViewFrame.class);
        when(viewFrame.getCurrentView(any())).thenReturn(root);

        ViewConfig config = mock(ViewConfig.class);
        when(config.getOptions()).thenReturn(mock(HashMap.class));
        when(config.isOptionSet(eq(CANCEL_ON_DROP))).thenReturn(true);
        when(config.getOptionValue(eq(CANCEL_ON_DROP))).thenReturn(true);

        IFContext context = createContextMock(root, IFContext.class);
        when(context.getConfig()).thenReturn(config);
        when(root.getContextByViewer(any())).thenReturn(context);

        new IFInventoryListener(viewFrame).onItemDrop(event);
        verify(root).getContextByViewer(any());
        verify(event).setCancelled(eq(true));
    }

    @Test
    void skipItemDropEventIfRootIsNotFound() {
        RootView root = createRootMock();
        PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
        ViewFrame viewFrame = mock(ViewFrame.class);
        when(viewFrame.getCurrentView(any())).thenReturn(null);

        new IFInventoryListener(viewFrame).onItemDrop(event);
        verify(root, never()).getContextByViewer(any());
    }

    @Test
    void skipItemDropEventIfConfigIsNotSet() {
        RootView root = createRootMock();
        PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
        ViewFrame viewFrame = mock(ViewFrame.class);
        when(viewFrame.getCurrentView(any())).thenReturn(root);

        ViewConfig config = mock(ViewConfig.class);
        when(config.getOptions()).thenReturn(mock(HashMap.class));
        when(config.isOptionSet(eq(CANCEL_ON_DROP))).thenReturn(false);

        IFContext context = createContextMock(root, IFContext.class);
        when(context.getConfig()).thenReturn(config);
        when(root.getContextByViewer(any())).thenReturn(context);

        new IFInventoryListener(viewFrame).onItemDrop(event);
        verify(root).getContextByViewer(any());
        verify(config, never()).getOptionValue(eq(CANCEL_ON_DROP));
        verify(event, never()).setCancelled(eq(true));
    }

    @Test
    void shouldCancelItemPickup() {
        RootView root = createRootMock();
        PlayerPickupItemEvent event = mock(PlayerPickupItemEvent.class);
        ViewFrame viewFrame = mock(ViewFrame.class);
        when(viewFrame.getCurrentView(any())).thenReturn(root);

        ViewConfig config = mock(ViewConfig.class);
        when(config.getOptions()).thenReturn(mock(HashMap.class));
        when(config.isOptionSet(eq(CANCEL_ON_PICKUP))).thenReturn(true);
        when(config.getOptionValue(eq(CANCEL_ON_PICKUP))).thenReturn(true);

        IFContext context = createContextMock(root, IFContext.class);
        when(context.getConfig()).thenReturn(config);
        when(root.getContextByViewer(any())).thenReturn(context);

        new IFInventoryListener(viewFrame).onItemPickup(event);
        verify(root).getContextByViewer(any());
        verify(event).setCancelled(eq(true));
    }

    @Test
    void skipItemPickupEventIfRootIsNotFound() {
        RootView root = createRootMock();
        ViewFrame viewFrame = mock(ViewFrame.class);
        when(viewFrame.getCurrentView(any())).thenReturn(null);

        new IFInventoryListener(viewFrame).onItemPickup(mock(PlayerPickupItemEvent.class));
        verify(root, never()).getContextByViewer(any());
    }

    @Test
    void skipItemPickupEventIfConfigIsNotSet() {
        RootView root = createRootMock();
        PlayerPickupItemEvent event = mock(PlayerPickupItemEvent.class);
        ViewFrame viewFrame = mock(ViewFrame.class);
        when(viewFrame.getCurrentView(any())).thenReturn(root);

        ViewConfig config = mock(ViewConfig.class);
        when(config.getOptions()).thenReturn(mock(HashMap.class));
        when(config.isOptionSet(eq(CANCEL_ON_PICKUP))).thenReturn(false);

        IFContext context = createContextMock(root, IFContext.class);
        when(context.getConfig()).thenReturn(config);
        when(root.getContextByViewer(any())).thenReturn(context);

        new IFInventoryListener(viewFrame).onItemPickup(event);
        verify(root).getContextByViewer(any());
        verify(config, never()).getOptionValue(eq(CANCEL_ON_PICKUP));
        verify(event, never()).setCancelled(eq(true));
    }
}
