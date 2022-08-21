package me.saiintbrisson.minecraft.pipeline.interceptors;

import static me.saiintbrisson.minecraft.AbstractView.RENDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.BaseViewContext;
import me.saiintbrisson.minecraft.ViewContainer;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.ViewType;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.exception.ContainerException;
import me.saiintbrisson.minecraft.exception.SlotFillExceededException;
import me.saiintbrisson.minecraft.exception.UnresolvedLayoutException;
import me.saiintbrisson.minecraft.pipeline.Pipeline;
import org.junit.jupiter.api.Test;

public class AvailableSlotRenderInterceptorTest {

    private static final String[] LAYOUT = {"XXXXXXXXX", "XOOOOOOOX", "XXXXXXXXX"};
    private static final int[] ITEMS_LAYER_SLOTS = {10, 11, 12, 13, 14, 15, 16};

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
        AbstractView root = createAbstractViewWithLayoutLength();
        root.setLayout(LAYOUT);
        root.availableSlot(itemType);

        // set it manually here because it defined on resolution interceptor,
        // and we are only testing AvailableSlotRenderInterceptor
        int expectedSlot = 10;
        applyLayoutItemsLayerWithReservedItemsOn(root, expectedSlot);

        when(context.getRoot()).thenReturn(root);

        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        pipeline.execute(RENDER, context);

        ViewItem item = root.getItem(expectedSlot);
        assertNotNull(item, "An item must be set at the target slot respecting layout items layer");
        assertEquals(itemType, item.getItem(), "Targeted item type must have the same type as defined before");
    }

    @Test
    public void whenHaveMoreReservedItemsThanLayoutItemsLayerSizeThenThrowException() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new AvailableSlotRenderInterceptor());

        ViewContext context = mock(ViewContext.class);
        when(context.getLayout()).thenReturn(null);

        AbstractView root = createAbstractViewWithLayoutLength();
        root.setLayout(LAYOUT);

        // greater than layout items layer size
        int itemsLayerLength = ITEMS_LAYER_SLOTS.length;
        String baseItemName = "my-item-";

        int[] slots = new int[itemsLayerLength];
        for (int i = 0; i < itemsLayerLength; i++) {
            int idx = ITEMS_LAYER_SLOTS[i];
            slots[i] = idx;
            root.availableSlot(baseItemName + idx);
        }

        // exceeding item
        root.availableSlot("my-exceeding-item");

        // set it manually here because it defined on resolution interceptor,
        // and we are only testing AvailableSlotRenderInterceptor
        applyLayoutItemsLayerWithReservedItemsOn(root, slots);

        when(context.getRoot()).thenReturn(root);

        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        assertThrows(
                SlotFillExceededException.class,
                () -> pipeline.execute(RENDER, context),
                "An SlotFillExceededException must be thrown");

        assertEquals(itemsLayerLength, root.getLayoutItemsLayer().size());

        for (int renderedSlot : slots) {
            ViewItem item = root.getItem(renderedSlot);
            assertNotNull(item, "Non-exceeding items must be rendered");
            assertEquals(item.getItem(), baseItemName + renderedSlot);
        }
    }

    @Test
    public void givenRootAndContextReservedItemsThenShiftLeftItems() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new AvailableSlotRenderInterceptor() {
            @Override
            boolean isSuppressContainerException() {
                return true;
            }
        });

        BaseViewContext context = mock(BaseViewContext.class);
        when(context.getLayout()).thenReturn(null);

        AbstractView root = createAbstractViewWithLayoutLength();
        root.setLayout(LAYOUT);
        root.setLayoutSignatureChecked(false);

        // set it manually here because it defined on resolution interceptor,
        // and we are only testing AvailableSlotRenderInterceptor
        applyLayoutItemsLayerWithReservedItemsOn(root, 10);
        root.availableSlot("root-item");

        when(context.getItems()).thenReturn(new ViewItem[root.getItems().length]);

        Deque<ViewItem> reservedItems = new ArrayDeque<>(2);
        reservedItems.add(new ViewItem(ViewItem.AVAILABLE).withItem("context-item-1"));
        reservedItems.add(new ViewItem(ViewItem.AVAILABLE).withItem("context-item-2"));
        when(context.getReservedItems()).thenReturn(reservedItems);
        when(context.getRoot()).thenReturn(root);

        try {
            pipeline.execute(RENDER, context);
        } catch (ContainerException ignored) {
            // will be called cause there's no container available on context to render
        }

        assertEquals(ITEMS_LAYER_SLOTS.length, root.getLayoutItemsLayer().size());
        assertNull(context.getLayoutItemsLayer());

        ViewItem rootItem = root.getItem(ITEMS_LAYER_SLOTS[0]);
        assertNotNull(rootItem, String.format("Root defined item must be present at %d", ITEMS_LAYER_SLOTS[0]));
        assertEquals("root-item", rootItem.getItem(), "Root defined item doesn't match");

        ViewItem contextItem1 = context.getItem(ITEMS_LAYER_SLOTS[1]);
        assertNotNull(
                contextItem1, String.format("Context defined item 1 must be present at %d", ITEMS_LAYER_SLOTS[1]));
        assertEquals("context-item-1", contextItem1.getItem(), "Context defined item 1 doesn't match");

        ViewItem contextItem2 = context.getItem(ITEMS_LAYER_SLOTS[2]);
        assertNotNull(
                contextItem2, String.format("Context defined item 2 must be present at %d", ITEMS_LAYER_SLOTS[1]));
        assertEquals("context-item-2", contextItem2.getItem(), "Context defined item 2 doesn't match");
    }

    @Test
    public void givenRootAndContextReservedItemsThenShiftLeftItemsMultiple() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new AvailableSlotRenderInterceptor() {
            @Override
            boolean isSuppressContainerException() {
                return true;
            }
        });

        BaseViewContext context = mock(BaseViewContext.class);
        when(context.getLayout()).thenReturn(null);

        AbstractView root = createAbstractViewWithLayoutLength();
        root.setLayout(LAYOUT);
        root.setLayoutSignatureChecked(false);

        // set it manually here because it defined on resolution interceptor,
        // and we are only testing AvailableSlotRenderInterceptor
        applyLayoutItemsLayerWithReservedItemsOn(root, 10, 11);
        root.availableSlot("root-item-1");
        root.availableSlot("root-item-2");

        when(context.getItems()).thenReturn(new ViewItem[root.getItems().length]);

        Deque<ViewItem> reservedItems = new ArrayDeque<>(2);
        reservedItems.add(new ViewItem(ViewItem.AVAILABLE).withItem("context-item-1"));
        reservedItems.add(new ViewItem(ViewItem.AVAILABLE).withItem("context-item-2"));
        when(context.getReservedItems()).thenReturn(reservedItems);
        when(context.getRoot()).thenReturn(root);

        try {
            pipeline.execute(RENDER, context);
        } catch (ContainerException ignored) {
            // will be called cause there's no container available on context to render
        }

        assertEquals(ITEMS_LAYER_SLOTS.length, root.getLayoutItemsLayer().size());
        assertNull(context.getLayoutItemsLayer());

        ViewItem rootItem1 = root.getItem(ITEMS_LAYER_SLOTS[0]);
        assertNotNull(rootItem1, String.format("Root defined item 1 must be present at %d", ITEMS_LAYER_SLOTS[0]));
        assertEquals("root-item-1", rootItem1.getItem(), "Root defined 1 item doesn't match");

        ViewItem rootItem2 = root.getItem(ITEMS_LAYER_SLOTS[1]);
        assertNotNull(rootItem2, String.format("Root defined item 2 must be present at %d", ITEMS_LAYER_SLOTS[0]));
        assertEquals("root-item-2", rootItem2.getItem(), "Root defined item 2 doesn't match");

        ViewItem contextItem1 = context.getItem(ITEMS_LAYER_SLOTS[2]);
        assertNotNull(
                contextItem1, String.format("Context defined item 1 must be present at %d", ITEMS_LAYER_SLOTS[1]));
        assertEquals("context-item-1", contextItem1.getItem(), "Context defined item 1 doesn't match");

        ViewItem contextItem2 = context.getItem(ITEMS_LAYER_SLOTS[3]);
        assertNotNull(
                contextItem2, String.format("Context defined item 2 must be present at %d", ITEMS_LAYER_SLOTS[1]));
        assertEquals("context-item-2", contextItem2.getItem(), "Context defined item 2 doesn't match");
    }

    private AbstractView createAbstractViewWithLayoutLength() {
        return new AbstractView(LAYOUT.length, null, ViewType.CHEST) {};
    }

    /**
     * Setups layout items layer to be rendered based on the given slots.
     *
     * @param view  The target view.
     * @param slots The slots that'll be on the layout items layer.
     */
    private void applyLayoutItemsLayerWithReservedItemsOn(VirtualView view, int... slots) {
        Stack<Integer> layoutItemsLayer = new Stack<>();
        for (int i : ITEMS_LAYER_SLOTS) layoutItemsLayer.add(i);

        view.setLayoutItemsLayer(layoutItemsLayer);
        if (view instanceof AbstractView) ((AbstractView) view).setReservedItemsCount(slots.length);

        view.setLayoutSignatureChecked(true);
    }
}
