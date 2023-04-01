package me.devnatan.inventoryframework;

import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.component.PaginationElementFactory;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class PaginationStateBuilder<
        TContext extends IFContext,
        TSlotContext extends IFSlotContext,
        I extends ItemComponentBuilder<I> & ComponentFactory,
        V> {

    private final PlatformView<I, TContext, ?, ?, ?, TSlotContext, ?> root;
    private final Object sourceProvider;
    private char layoutTarget = LayoutSlot.FILLED_RESERVED_CHAR;
    private PaginationElementFactory<V> elementFactory;
    private BiConsumer<TContext, Pagination> pageSwitchHandler;

    /**
     * Sets the element factory for pagination.
     * <p>
     * It consists of a function whose first parameter is a derivation of the
     * {@link ItemComponentBuilder} that must be used to configure the item, and the second
     * parameter is the current element being paginated.
     * <p>
     * This function is called for every single paginated element.
     *
     * @param elementFactory The element factory.
     * @return This pagination state builder.
     */
    public PaginationStateBuilder<TContext, TSlotContext, I, V> elementFactory(
            @NotNull PaginationElementFactory<V> elementFactory) {
        this.elementFactory = elementFactory;
        return this;
    }

    /**
     * Sets the item factory for pagination.
     * <p>
     * It consists of a function whose first parameter is a derivation of the
     * {@link ItemComponentBuilder} that must be used to configure the item, and the second
     * parameter is the current element being paginated.
     * <p>
     * This function is called for every single paginated element.
     *
     * @param itemFactory The item factory.
     * @return This pagination state builder.
     */
    public PaginationStateBuilder<TContext, TSlotContext, I, V> itemFactory(@NotNull BiConsumer<I, V> itemFactory) {
        return elementFactory((index, slot, value) -> {
            @SuppressWarnings("unchecked")
            I builder = (I) root.getElementFactory().createComponentBuilder();
            builder.withSlot(slot);
            itemFactory.accept(builder, value);
            return builder;
        });
    }

    /**
     * Defines a target character in the layout whose pagination will be rendered.
     * <p>
     * By default, if there is a layout available and a target character has not
     * been explicitly  defined in the layout, the layout's renderization target
     * character will be the {@link LayoutSlot#FILLED_RESERVED_CHAR reserved layout character}.
     * <p>
     * If there is no layout configured, pagination will be rendered throughout the view container.
     *
     * @param layoutTarget The target layout character.
     * @return This pagination state builder.
     */
    public PaginationStateBuilder<TContext, TSlotContext, I, V> layoutTarget(char layoutTarget) {
        this.layoutTarget = layoutTarget;
        return this;
    }

    /**
     * Handles the page switching action.
     * <p>
     * The first parameter is the previous page and the current page can be
     * obtained through {@link Pagination#currentPage()}.
     *
     * @param pageSwitchHandler The page switch handler.
     * @return This pagination state builder.
     */
    public PaginationStateBuilder<TContext, TSlotContext, I, V> onPageSwitch(
            @NotNull BiConsumer<TContext, Pagination> pageSwitchHandler) {
        this.pageSwitchHandler = pageSwitchHandler;
        return this;
    }

    /**
     * Builds a pagination state based on this builder values.
     *
     * @return A new {@link Pagination} state.
     */
    public State<Pagination> build() {
        return root.buildPaginationState(this);
    }
}
