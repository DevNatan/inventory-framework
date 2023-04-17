package me.devnatan.inventoryframework.component;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import org.junit.jupiter.api.Test;

public class PaginationImplTest {

    @Test
    void callAnyChildInteractionHandlerOnClickInteraction() {
        PaginationImpl pagination = mock(PaginationImpl.class);
        doCallRealMethod().when(pagination).clicked(any(), any());

        Component child = mock(Component.class);
        InteractionHandler interactionHandler = mock(InteractionHandler.class);
        when(child.getInteractionHandler()).thenReturn(interactionHandler);
        when(child.isContainedWithin(0)).thenReturn(true);
        when(pagination.getComponentsInternal()).thenReturn(Collections.singletonList(child));

        IFSlotClickContext clickContext = mock(IFSlotClickContext.class);
        when(clickContext.getClickedSlot()).thenReturn(0);
        pagination.clicked(pagination, clickContext);
        verify(interactionHandler, atLeastOnce()).clicked(pagination, clickContext);
    }

    @Test
    void callCorrectChildInteractionHandlerOnClickInteraction() {
        PaginationImpl pagination = mock(PaginationImpl.class);
        doCallRealMethod().when(pagination).clicked(any(), any());

        InteractionHandler child0InteractionHandler = mock(InteractionHandler.class);
        Component childAt0 = mock(Component.class);
        when(childAt0.isContainedWithin(0)).thenReturn(true);
        when(childAt0.isContainedWithin(1)).thenReturn(false);
        when(childAt0.getInteractionHandler()).thenReturn(child0InteractionHandler);

        InteractionHandler child1InteractionHandler = mock(InteractionHandler.class);
        Component childAt1 = mock(Component.class);
        when(childAt1.isContainedWithin(0)).thenReturn(false);
        when(childAt1.isContainedWithin(1)).thenReturn(true);
        when(childAt1.getInteractionHandler()).thenReturn(child1InteractionHandler);
        when(pagination.getComponentsInternal()).thenReturn(Arrays.asList(childAt0, childAt1));

        IFSlotClickContext clickContext = mock(IFSlotClickContext.class);
        when(clickContext.getClickedSlot()).thenReturn(0);
        pagination.clicked(pagination, clickContext);
        verify(child0InteractionHandler, times(1)).clicked(pagination, clickContext);
        verify(child1InteractionHandler, never()).clicked(pagination, clickContext);
    }
}
