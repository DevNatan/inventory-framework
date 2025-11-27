package me.devnatan.inventoryframework.component;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class PaginationStateBuilder<
        Context extends IFContext, Builder extends ItemComponentBuilder<Builder, Context> & ComponentFactory, V> {

    private final Supplier<ElementFactory> internalElementFactoryProvider;
    private final Object sourceProvider;
    private final Function<PaginationStateBuilder<Context, Builder, V>, State<Pagination>> internalStateFactory;
    private char layoutTarget = LayoutSlot.FILLED_RESERVED_CHAR;
    private PaginationElementFactory<V> paginationElementFactory;
    private BiConsumer<Context, Pagination> pageSwitchHandler;
    private final boolean async, computed;
    private Pagination.Orientation orientation;

    public PaginationStateBuilder(
            Supplier<ElementFactory> internalElementFactoryProvider,
            Object sourceProvider,
            Function<PaginationStateBuilder<Context, Builder, V>, State<Pagination>> internalStateFactory,
            boolean async,
            boolean computed) {
        this.internalElementFactoryProvider = internalElementFactoryProvider;
        this.internalStateFactory = internalStateFactory;
        this.sourceProvider = sourceProvider;
        this.async = async;
        this.computed = computed;
        this.orientation = Pagination.Orientation.HORIZONTAL;
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
    public PaginationStateBuilder<Context, Builder, V> itemFactory(@NotNull BiConsumer<Builder, V> itemFactory) {
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
    public PaginationStateBuilder<Context, Builder, V> elementFactory(
            @NotNull PaginationValueConsumer<Context, Builder, V> elementConsumer) {
        this.paginationElementFactory = (pagination, index, slot, value) -> {
            Context context = (Context) pagination.getRoot();
            Builder builder = (Builder) internalElementFactoryProvider.get().createComponentBuilder(pagination);
            builder.withSlot(slot).withExternallyManaged(true);
            elementConsumer.accept(context, builder, index, value);
            return builder;
        };
        return this;
    }

    /**
     * Defines a target character in the layout whose pagination will be rendered.
     * <p>
     * By default, if there is a layout available and a target character has not
     * been explicitly  defined in the layout, the layout's rendering target
     * character will be the {@link LayoutSlot#FILLED_RESERVED_CHAR reserved layout character}.
     * <p>
     * If there is no layout configured, pagination will be rendered throughout the view container.
     *
     * @param layoutTarget The target layout character.
     * @return This pagination state builder.
     */
    public PaginationStateBuilder<Context, Builder, V> layoutTarget(char layoutTarget) {
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
    public PaginationStateBuilder<Context, Builder, V> onPageSwitch(
            @NotNull BiConsumer<Context, Pagination> pageSwitchHandler) {
        this.pageSwitchHandler = pageSwitchHandler;
        return this;
    }

    /**
     * Defines the pagination iteration order.
     * Default value is {@link Pagination.Orientation#HORIZONTAL}.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param orientation The pagination orientation.
     * @return This pagination builder.
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Pagination#pagination-orientation">Pagination Orientation on Wiki</a>
     */
    @ApiStatus.Experimental
    public PaginationStateBuilder<Context, Builder, V> orientation(Pagination.Orientation orientation) {
        this.orientation = orientation;
        return this;
    }

    /**
     * Builds a pagination state based on this builder values.
     *
     * @return A new {@link Pagination} state.
     * @throws IllegalStateException If the element factory wasn't set.
     */
    public State<Pagination> build() {
        if (paginationElementFactory == null)
            throw new IllegalStateException(String.format(
                    "Element factory from #buildPaginationState(...) cannot be null. Set it using %s or %s.",
                    "#elementFactory(PaginationElementFactory)", "#itemFactory(BiConsumer)"));

        return internalStateFactory.apply(this);
    }

    public char getLayoutTarget() {
        return layoutTarget;
    }

    public Object getSourceProvider() {
        return sourceProvider;
    }

    public boolean isAsync() {
        return async;
    }

    public boolean isComputed() {
        return computed;
    }

    public BiConsumer<Context, Pagination> getPageSwitchHandler() {
        return pageSwitchHandler;
    }

    public PaginationElementFactory<V> getPaginationElementFactory() {
        return paginationElementFactory;
    }

    public Pagination.Orientation getOrientation() {
        return orientation;
    }
}
