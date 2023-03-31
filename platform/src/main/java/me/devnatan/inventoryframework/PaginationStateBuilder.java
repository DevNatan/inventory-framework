package me.devnatan.inventoryframework;

import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.component.PaginationElementFactory;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

// TODO needs documentation
@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class PaginationStateBuilder<
        C extends IFSlotContext, I extends ItemComponentBuilder<I> & ComponentFactory, V> {

    private final PlatformView<I, ?, ?, ?, ?, C, ?> root;
    private final Object sourceProvider;

    private char layoutTarget = LayoutSlot.FILLED_RESERVED_CHAR;
    private PaginationElementFactory<V> elementFactory;

    public PaginationStateBuilder<C, I, V> elementFactory(@NotNull PaginationElementFactory<V> elementFactory) {
        this.elementFactory = elementFactory;
        return this;
    }

    public PaginationStateBuilder<C, I, V> itemFactory(@NotNull BiConsumer<I, V> itemFactory) {
        return elementFactory((index, slot, value) -> {
            @SuppressWarnings("unchecked")
            I builder = (I) root.getElementFactory().createComponentBuilder();
            builder.withSlot(slot);
            itemFactory.accept(builder, value);
            return builder;
        });
    }

    public PaginationStateBuilder<C, I, V> layoutTarget(char layoutTarget) {
        this.layoutTarget = layoutTarget;
        return this;
    }

    public State<Pagination> build() {
        return root.buildPaginationState(this);
    }
}
