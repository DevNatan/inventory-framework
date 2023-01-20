package me.saiintbrisson.minecraft;

import static me.saiintbrisson.minecraft.AbstractView.RENDER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.saiintbrisson.minecraft.pipeline.interceptors.LayoutPatternApplierInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.LayoutResolutionInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.NavigationControllerInterceptor;
import me.saiintbrisson.minecraft.pipeline.interceptors.PaginationRenderInterceptor;
import org.junit.jupiter.api.Test;

public class UserDefinedPatternPaginationAccessTest {

    private static final char FILLER_CHAR = '-';
    private final String[] layout = {"---------", "XOOOOOOOX", "XOOOOOOOX", "XOOOOOOOX", "---------"};

    @Test
    void shouldAccessPaginationOnLayoutFactory() {
        ViewContainer container = TestUtils.createContainer(9, 5);
        AbstractPaginatedView<String> root = TestUtils.createInitializedPaginatedView(5);
        IFPaginatedContext<String> context = TestUtils.createPaginatedContext(root, container);
        context.setItems(new IFItem[54]); // initialize since it's set only on onOpen

        List<String> source = Arrays.asList("A", "B", "C", "D", "E", "F");
        context.setSource(source); // needed to initialize paginator
        context.setLayout(FILLER_CHAR, $ -> {
            assertDoesNotThrow(context::hasNextPage);
            assertEquals(source, context.getSource());
        });
        context.setLayout(layout);

        Pipeline<VirtualView> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new LayoutResolutionInterceptor());
        pipeline.intercept(RENDER, createPaginationRenderInterceptor());
        pipeline.intercept(RENDER, new LayoutPatternApplierInterceptor());
        pipeline.intercept(RENDER, new NavigationControllerInterceptor());
        assertDoesNotThrow(() -> pipeline.execute(RENDER, context));
    }

    @Test
    void shouldAccessDynamicPaginationOnLayoutFactory() {
        ViewContainer container = TestUtils.createContainer(9, 5);
        AbstractPaginatedView<String> root = TestUtils.createInitializedPaginatedView(5);
        IFPaginatedContext<String> context = TestUtils.createPaginatedContext(root, container);
        context.setItems(new IFItem[54]); // initialize since it's set only on onOpen

        List<String> source = Arrays.asList("A", "B", "C", "D", "E", "F");
        context.setSource($ -> source); // needed to initialize paginator
        context.setLayout(FILLER_CHAR, $ -> {
            assertDoesNotThrow(context::hasNextPage);
            assertEquals(source, context.getSource());
        });
        context.setLayout(layout);

        Pipeline<VirtualView> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new LayoutResolutionInterceptor());
        pipeline.intercept(RENDER, createPaginationRenderInterceptor());
        pipeline.intercept(RENDER, new LayoutPatternApplierInterceptor());
        pipeline.intercept(RENDER, new NavigationControllerInterceptor());
        assertDoesNotThrow(() -> pipeline.execute(RENDER, context));
    }

    private PaginationRenderInterceptor createPaginationRenderInterceptor() {
        PaginationRenderInterceptor interceptor = new PaginationRenderInterceptor();
        interceptor.skipRender = true;
        return interceptor;
    }
}
