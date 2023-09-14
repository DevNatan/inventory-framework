package me.devnatan.inventoryframework.component;

import java.util.function.BiConsumer;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public final class PaginationBuilder<
	Context extends IFContext,
	Builder extends ItemComponentBuilder<Builder, Context> & ComponentFactory,
	V> extends DefaultComponentBuilder<PaginationBuilder<Context, Builder, V>, Context> {

    private final PlatformView root;
    private final Object sourceProvider;
    private char layoutTarget = LayoutSlot.FILLED_RESERVED_CHAR;
    private PaginationElementFactory<V> elementFactory;
    private BiConsumer<Context, Pagination> pageSwitchHandler;
    private final boolean async, computed;

    PaginationBuilder(PlatformView root, Object sourceProvider) {
        this(root, sourceProvider, false, false);
    }

    PaginationBuilder(PlatformView root, Object sourceProvider, boolean async, boolean computed) {
        this.root = root;
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
    public PaginationBuilder<Context, Builder, V> itemFactory(@NotNull BiConsumer<Builder, V> itemFactory) {
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
    public PaginationBuilder<Context, Builder, V> elementFactory(
            @NotNull PaginationValueConsumer<Context, Builder, V> elementConsumer) {
        this.elementFactory = (pagination, index, slot, value) -> {
            Context context = (Context) pagination.getRoot();
            Builder builder = (Builder) root.getElementFactory().createComponentBuilder(pagination);
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
    public PaginationBuilder<Context, Builder, V> layoutTarget(char layoutTarget) {
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
    public PaginationBuilder<Context, Builder, V> onPageSwitch(
            @NotNull BiConsumer<Context, Pagination> pageSwitchHandler) {
        this.pageSwitchHandler = pageSwitchHandler;
        return this;
    }

    /**
     * Builds a pagination state based on this builder values.
     *
     * @return A new {@link Pagination} state.
     * @throws IllegalStateException If the element factory wasn't set.
     */
    @SuppressWarnings("unchecked")
    public State<Pagination> build() {
        if (elementFactory == null)
            throw new IllegalStateException(String.format(
                    "Element factory from #buildPaginationState(...) cannot be null. Set it using %s or %s.",
                    "#elementFactory(PaginationElementFactory)", "#itemFactory(BiConsumer)"));

        return root.createPaginationState(this);
    }

    PlatformView getRoot() {
        return root;
    }

    Object getSourceProvider() {
        return sourceProvider;
    }

    char getLayoutTarget() {
        return layoutTarget;
    }

    PaginationElementFactory<V> getElementFactory() {
        return elementFactory;
    }

    BiConsumer<Context, Pagination> getPageSwitchHandler() {
        return pageSwitchHandler;
    }

    boolean isAsync() {
        return async;
    }

    boolean isComputed() {
        return computed;
    }
}
