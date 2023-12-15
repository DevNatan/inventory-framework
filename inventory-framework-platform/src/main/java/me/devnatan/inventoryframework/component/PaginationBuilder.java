package me.devnatan.inventoryframework.component;

import java.util.function.BiConsumer;
import java.util.function.Function;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.internal.PlatformUtils;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class PaginationBuilder<CONTEXT, ITEM_BUILDER, V>
        extends PlatformComponentBuilder<PaginationBuilder<CONTEXT, ITEM_BUILDER, V>, CONTEXT> {

    private final Object sourceProvider;
    private char layoutTarget = LayoutSlot.DEFAULT_SLOT_FILL_CHAR;
    private PaginationElementFactory<V> elementFactory;
    private BiConsumer<CONTEXT, Pagination> pageSwitchHandler;
    private final boolean async, computed;
    private final Function<PaginationBuilder<CONTEXT, ITEM_BUILDER, V>, State<Pagination>> stateFactory;

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public PaginationBuilder(
            Object sourceProvider,
            boolean async,
            boolean computed,
            Function<PaginationBuilder<CONTEXT, ITEM_BUILDER, V>, State<Pagination>> stateFactory) {
        this.sourceProvider = sourceProvider;
        this.async = async;
        this.computed = computed;
        this.stateFactory = stateFactory;
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
    @SuppressWarnings("unchecked")
    public PaginationBuilder<CONTEXT, ITEM_BUILDER, V> itemFactory(@NotNull BiConsumer<ITEM_BUILDER, V> itemFactory) {
        this.elementFactory = (pagination, index, slot, value) -> {
            ItemComponentBuilder builder = PlatformUtils.getFactory().createItemComponentBuilder(pagination);
            builder.setPosition(slot);
            itemFactory.accept((ITEM_BUILDER) builder, value);
            return builder.buildComponent(pagination);
        };
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
     * @param elementConsumer The element consumer.
     * @return This pagination state builder.
     */
    @SuppressWarnings("unchecked")
    public PaginationBuilder<CONTEXT, ITEM_BUILDER, V> elementFactory(
            @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, V> elementConsumer) {
        this.elementFactory = (pagination, index, slot, value) -> {
            CONTEXT context = (CONTEXT) pagination.getContext();
            ItemComponentBuilder builder = PlatformUtils.getFactory().createItemComponentBuilder(pagination);
            builder.setPosition(slot);

            elementConsumer.accept(context, (ITEM_BUILDER) builder, index, value);

            return builder.buildComponent(pagination);
        };
        return this;
    }

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @SuppressWarnings("rawtypes")
    @ApiStatus.Experimental
    public PaginationBuilder<CONTEXT, ITEM_BUILDER, V> componentFactory(
            PaginationValueComponentFactory<CONTEXT, V> factory) {
        this.elementFactory = (pagination, index, slot, value) -> {
            @SuppressWarnings("unchecked")
            final PlatformComponentBuilder builder =
                    (PlatformComponentBuilder) factory.accept((CONTEXT) pagination.getContext(), index, value);
            builder.withSlot(slot);
            builder.withSelfManaged(PaginationBuilder.this.isSelfManaged());
            final Component component = builder.buildComponent(pagination);
            component.setHandle(builder.buildHandle());
            return component;
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
    public PaginationBuilder<CONTEXT, ITEM_BUILDER, V> layoutTarget(char layoutTarget) {
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
    public PaginationBuilder<CONTEXT, ITEM_BUILDER, V> onPageSwitch(
            @NotNull BiConsumer<CONTEXT, Pagination> pageSwitchHandler) {
        this.pageSwitchHandler = pageSwitchHandler;
        return this;
    }

    /**
     * Builds a {@link Pagination} state from this pagination builder.
     *
     * @return A new pagination state.
     */
    public State<Pagination> build() {
        return stateFactory.apply(this);
    }

    @Override
    public Component buildComponent(VirtualView root) {
        throw new UnsupportedOperationException("PaginationBuilder component cannot be built");
    }

    @Override
    public ComponentHandle buildHandle() {
        throw new UnsupportedOperationException("PaginationBuilder component cannot be built");
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Pagination buildComponent0(long stateId, VirtualView root) {
        return new PaginationImpl(
                Long.toString(stateId),
                root,
                getReference(),
                getWatchingStates(),
                getDisplayCondition(),
                stateId,
                layoutTarget,
                sourceProvider,
                (PaginationElementFactory) elementFactory,
                (BiConsumer) pageSwitchHandler,
                async,
                computed,
                isSelfManaged());
    }
}
