package me.devnatan.inventoryframework.component;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.PaginationState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValueFactory;
import org.jetbrains.annotations.NotNull;

public final class PaginationStateBuilder<CONTEXT extends IFContext, ITEM_BUILDER extends ItemComponentBuilder<ITEM_BUILDER>, V> extends PlatformComponentBuilder<PaginationStateBuilder<CONTEXT, ITEM_BUILDER, V>, CONTEXT> {

    private final ElementFactory internalElementFactory;
    private final Object sourceProvider;
    private char layoutTarget = LayoutSlot.FILLED_RESERVED_CHAR;
    private PaginationElementFactory<V> paginationElementFactory;
    private BiConsumer<CONTEXT, Pagination> pageSwitchHandler;
    private final boolean async, computed;

    public PaginationStateBuilder(
		Ref<Component> reference,
		Map<String, Object> data,
		boolean cancelOnClick,
		boolean closeOnClick,
		boolean updateOnClick,
		Set<State<?>> watchingStates,
		boolean isManagedExternally,
		Predicate<? extends IFContext> displayCondition,
		ElementFactory internalElementFactory,
		Object sourceProvider,
		boolean async,
		boolean computed
	) {
		super(reference, data, cancelOnClick, closeOnClick, updateOnClick, watchingStates, isManagedExternally, displayCondition);
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
    public PaginationStateBuilder<CONTEXT, ITEM_BUILDER, V> itemFactory(@NotNull BiConsumer<ITEM_BUILDER, V> itemFactory) {
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
    public PaginationStateBuilder<CONTEXT, ITEM_BUILDER, V> elementFactory(
            @NotNull PaginationValueConsumer<CONTEXT, ITEM_BUILDER, V> elementConsumer) {
        this.paginationElementFactory = (pagination, index, slot, value) -> {
            CONTEXT context = (CONTEXT) pagination.getRoot();
            ITEM_BUILDER builder = (ITEM_BUILDER) internalElementFactory.createComponentBuilder(pagination);
            builder.withSlot(slot).withExternallyManaged(true);
            elementConsumer.accept(context, builder, index, value);

			return new ComponentFactory() {
				@Override
				public @NotNull Component create() {
					return builder.build(context);
				}
			};
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
    public PaginationStateBuilder<CONTEXT, ITEM_BUILDER, V> layoutTarget(char layoutTarget) {
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
    public PaginationStateBuilder<CONTEXT, ITEM_BUILDER, V> onPageSwitch(
            @NotNull BiConsumer<CONTEXT, Pagination> pageSwitchHandler) {
        this.pageSwitchHandler = pageSwitchHandler;
        return this;
    }

//    /**
//     * Builds a pagination state based on this builder values.
//     *
//     * @return A new {@link Pagination} state.
//     * @throws IllegalStateException If the element factory wasn't set.
//     */
//    public State<Pagination> build() {
//        if (paginationElementFactory == null)
//            throw new IllegalStateException(String.format(
//                    "Element factory from #buildPaginationState(...) cannot be null. Set it using %s or %s.",
//                    "#elementFactory(PaginationElementFactory)", "#itemFactory(BiConsumer)"));
//
//        return internalStateFactory.apply(this);
//    }
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Component build(VirtualView root) {
		return new PaginationImpl(
			State.next(),
			root,
			reference,
			watchingStates,
			layoutTarget,
			sourceProvider,
			(PaginationElementFactory) paginationElementFactory,
			(BiConsumer) pageSwitchHandler,
			async,
			computed
		);
	}
}
