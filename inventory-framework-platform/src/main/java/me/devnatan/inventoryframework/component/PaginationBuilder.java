package me.devnatan.inventoryframework.component;

import java.util.function.BiConsumer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class PaginationBuilder<CONTEXT, BUILDER, V>
        extends PlatformComponentBuilder<PaginationBuilder<CONTEXT, BUILDER, V>, CONTEXT> {

    private final ElementFactory internalElementFactory;
    private final Object sourceProvider;
    private char layoutTarget = LayoutSlot.DEFAULT_SLOT_FILL_CHAR;
    private PaginationElementFactory<V> paginationElementFactory;
    private BiConsumer<CONTEXT, Pagination> pageSwitchHandler;
    private final boolean async, computed;

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public PaginationBuilder(
            ElementFactory internalElementFactory, Object sourceProvider, boolean async, boolean computed) {
        this.internalElementFactory = internalElementFactory;
        this.sourceProvider = sourceProvider;
        this.async = async;
        this.computed = computed;
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
    public PaginationBuilder<CONTEXT, BUILDER, V> itemFactory(@NotNull BiConsumer<BUILDER, V> itemFactory) {
        return elementFactory(((context, builder, index, value) -> itemFactory.accept(builder, value)));
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
     * @param elementConsumer The element consumer.
     * @return This pagination state builder.
     */
    @SuppressWarnings("unchecked")
    public PaginationBuilder<CONTEXT, BUILDER, V> elementFactory(
            @NotNull PaginationValueConsumer<CONTEXT, BUILDER, V> elementConsumer) {
        this.paginationElementFactory = (pagination, index, slot, value) -> {
            CONTEXT context = (CONTEXT) pagination.getRoot();
            BUILDER builder = (BUILDER) internalElementFactory.createComponentBuilder(pagination);
            if (builder instanceof ItemComponentBuilder) ((ItemComponentBuilder) builder).setPosition(slot);

            elementConsumer.accept(context, builder, index, value);

            return ((ComponentBuilder) builder).build(pagination);
        };
        return this;
    }

    /**
     * Defines a target character in the layout whose pagination will be rendered.
     * <p>
     * By default, if there is a layout available and a target character has not
     * been explicitly  defined in the layout, the layout's rendering target
     * character will be the {@link LayoutSlot#DEFAULT_SLOT_FILL_CHAR default layout character}.
     * <p>
     * If there is no layout configured, pagination will be rendered throughout the view container.
     *
     * @param layoutTarget The target layout character.
     * @return This pagination state builder.
     */
    public PaginationBuilder<CONTEXT, BUILDER, V> layoutTarget(char layoutTarget) {
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
    public PaginationBuilder<CONTEXT, BUILDER, V> onPageSwitch(
            @NotNull BiConsumer<CONTEXT, Pagination> pageSwitchHandler) {
        this.pageSwitchHandler = pageSwitchHandler;
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Component build(VirtualView root) {
        long stateId = State.next();
        return new PaginationImpl(
                Long.toString(stateId),
                root,
                getReference(),
                getWatchingStates(),
                getDisplayCondition(),
                stateId,
                layoutTarget,
                sourceProvider,
                (PaginationElementFactory) paginationElementFactory,
                (BiConsumer) pageSwitchHandler,
                async,
                computed);
    }
}
