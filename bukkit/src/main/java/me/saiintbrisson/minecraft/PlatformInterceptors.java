package me.saiintbrisson.minecraft;

import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.devnatan.inventoryframework.bukkit.View;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PlatformInterceptors {

    static final PipelineInterceptor<IFContext> open =
            createGeneric((view, context) -> view.onOpen((OpenViewContext) context));
    static final PipelineInterceptor<IFContext> close =
            createGeneric((view, context) -> view.onClose((ViewContext) context));
    static final PipelineInterceptor<IFContext> render =
            createGeneric((view, context) -> view.onRender((ViewContext) context));
    static final PipelineInterceptor<IFContext> update =
            createGeneric((view, context) -> view.onUpdate((ViewContext) context));
    static final PipelineInterceptor<IFContext> resume =
            createGeneric((view, context) -> view.onResume((ViewContext) context));

    @SuppressWarnings({"rawtypes", "unchecked"})
    static final PipelineInterceptor<IFContext> paginatedItemRender = createGeneric((view, context) -> {
        PaginatedViewSlotContext<?> paginatedContext = (PaginatedViewSlotContext<?>) context;
        ((PaginatedView) context.getRoot())
                .onItemRender(
                        paginatedContext,
                        ((AbstractViewSlotContext) context).getBackingItem(),
                        paginatedContext.getValue());
    });

    private static <T extends IFContext> PipelineInterceptor<T> createGeneric(BiConsumer<View, IFContext> call) {
        return (pipeline, context) -> call.accept((View) context.getRoot(), context);
    }
}
