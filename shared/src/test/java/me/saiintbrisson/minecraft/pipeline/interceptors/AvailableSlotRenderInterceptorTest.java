package me.saiintbrisson.minecraft.pipeline.interceptors;

import static me.saiintbrisson.minecraft.AbstractView.RENDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Stack;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.ViewContainer;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.ViewType;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.exception.UnresolvedLayoutException;
import me.saiintbrisson.minecraft.pipeline.Pipeline;
import org.junit.jupiter.api.Test;

public class AvailableSlotRenderInterceptorTest {

    private static final String[] LAYOUT = {"XXXXXXXXX", "XOOOOOOOX", "XXXXXXXXX"};

    @Test
    public void givenContextRenderWhenLayoutSignatureIsNotCheckedThenThrowException() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new AvailableSlotRenderInterceptor());

        ViewContext context = mock(ViewContext.class);
        when(context.getLayout()).thenReturn(LAYOUT);
        when(context.isLayoutSignatureChecked()).thenReturn(false);

        AbstractView root = mock(AbstractView.class);
        when(root.isLayoutSignatureChecked()).thenReturn(false /* skip root render */);
        when(context.getRoot()).thenReturn(root);

        assertThrows(
                UnresolvedLayoutException.class,
                () -> pipeline.execute(RENDER, context),
                "An exception must be throw when try to render layout without pre-resolution");
    }

    @Test
    public void shouldApplyReservedItemsOnRoot() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new AvailableSlotRenderInterceptor());

        ViewContext context = mock(ViewContext.class);
        when(context.getLayout()).thenReturn(null);

        String itemType = "my-item";
        AbstractView root = new AbstractView(LAYOUT.length, null, ViewType.CHEST) {};
        root.setLayout(LAYOUT);
        root.availableSlot(itemType);

        // set it manually here because it defined on resolution interceptor,
        // and we are only testing AvailableSlotRenderInterceptor
        Stack<Integer> layoutItemsLayer = new Stack<>();
        int expectedSlot = 10;
        layoutItemsLayer.add(expectedSlot);
        root.setLayoutItemsLayer(layoutItemsLayer);
        root.setReservedItemsCount(1);
        root.setLayoutSignatureChecked(true);

        when(context.getRoot()).thenReturn(root);

        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        pipeline.execute(RENDER, context);

        ViewItem item = root.getItems()[expectedSlot];
        assertNotNull(item, "An item must be set at the target slot respecting layout items layer");
        assertEquals(itemType, item.getItem(), "Targeted item type must have the same type as defined before");
    }
}
