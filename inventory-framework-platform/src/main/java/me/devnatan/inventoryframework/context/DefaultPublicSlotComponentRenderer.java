package me.devnatan.inventoryframework.context;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.utils.SlotConverter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public final class DefaultPublicSlotComponentRenderer<
                CONTEXT, BUILDER extends PlatformComponentBuilder<BUILDER, CONTEXT>>
        implements PublicSlotComponentRenderer<CONTEXT, BUILDER> {

    private final VirtualView root;
    private final IFRenderContext renderContext;
    private final Supplier<BUILDER> builderFactory;

    public DefaultPublicSlotComponentRenderer(
            VirtualView root, IFRenderContext renderContext, Supplier<BUILDER> builderFactory) {
        this.root = root;
        this.renderContext = renderContext;
        this.builderFactory = builderFactory;
    }

    private PlatformComponentBuilder<?, ?> createRegisteredBuilder() {
        final BUILDER builder = builderFactory.get();
        renderContext.getNotRenderedComponents().add(builder);
        return builder;
    }

    /**
     * Creates a new item builder without a specified slot.
     * <p>
     * This function is for creating items whose slot is set dynamically during item rendering.
     * <pre>{@code
     * unsetSlot().onRender(render -> {
     *     render.setItem(...);
     *     render.setSlot(...);
     * });
     * }</pre>
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @return An item builder to configure the item.
     */
    @ApiStatus.Experimental
    public BUILDER unsetSlot() {
        return (BUILDER) createRegisteredBuilder();
    }

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public BUILDER slot(int slot) {
        return (BUILDER) createRegisteredBuilder().withSlot(slot);
    }

    /**
     * Adds an item at the specific column and ROW (X, Y) in that context's container.
     *
     * @param row    The row (Y) in which the item will be positioned.
     * @param column The column (X) in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    @NotNull
    public BUILDER slot(int row, int column) {
        checkAlignedContainerTypeForSlotAssignment();
        return (BUILDER) createRegisteredBuilder().withSlot(row, column);
    }

    @Override
    public <T extends PlatformComponentBuilder<T, CONTEXT>> void slotComponent(int slot, T builder) {
        internalSlotComponent(slot, builder);
    }

    @Override
    public <T extends PlatformComponentBuilder<T, CONTEXT>> void slotComponent(int row, int column, T builder) {
        internalSlotComponent(
                SlotConverter.convertSlot(
                        row,
                        column,
                        renderContext.getContainer().getRowsCount(),
                        renderContext.getContainer().getColumnsCount()),
                builder);
    }

    /**
     * Sets an item in the first slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public BUILDER firstSlot() {
        return slot(renderContext.getContainer().getFirstSlot());
    }

    @Override
    public <T extends PlatformComponentBuilder<T, CONTEXT>> void firstSlotComponent(T builder) {
        internalSlotComponent(renderContext.getContainer().getFirstSlot(), builder);
    }

    /**
     * Sets an item in the last slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public BUILDER lastSlot() {
        return slot(renderContext.getContainer().getLastSlot());
    }

    @Override
    public <T extends PlatformComponentBuilder<T, CONTEXT>> void lastSlotComponent(T builder) {
        internalSlotComponent(renderContext.getContainer().getLastSlot(), builder);
    }

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public BUILDER resultSlot() {
        final ViewType containerType = renderContext.getContainer().getType();
        final int[] resultSlots = containerType.getResultSlots();
        if (resultSlots == null) throw new InventoryFrameworkException("No result slots available: " + containerType);

        if (resultSlots.length > 1)
            throw new InventoryFrameworkException("#resultSlot() do not support types with more than one result slot.");

        return slot(resultSlots[0]);
    }

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public BUILDER availableSlot() {
        final BUILDER builder = builderFactory.get();
        renderContext.getAvailableSlotFactories().add((index, slot) -> {
            ((PlatformComponentBuilder<?, ?>) builder).withSlot(slot);
            return builder;
        });
        return builder;
    }

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * <pre>{@code
     * availableSlot((index, builder) -> builder.withItem(...));
     * }</pre>
     *
     * @param factory A factory to create the item builder to configure the item.
     *                The first parameter is the iteration index of the available slot.
     */
    public void availableSlot(@NotNull BiConsumer<Integer, BUILDER> factory) {
        renderContext.getAvailableSlotFactories().add((index, slot) -> {
            final BUILDER builder = builderFactory.get();
            ((PlatformComponentBuilder<?, ?>) builder).withSlot(slot);
            factory.accept(index, builder);
            return builder;
        });
    }

    @Override
    public <T extends PlatformComponentBuilder<T, CONTEXT>> void availableSlotComponent(T builder) {
        throw new UnsupportedOperationException("Missing availableSlotComponent(T) implementation");
    }

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * @param character The layout character target.
     * @return An item builder to configure the item.
     */
    public BUILDER layoutSlot(char character) {
        // TODO More detailed exception message
        final LayoutSlot layoutSlot = renderContext.getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        final BUILDER builder = builderFactory.get();
        renderContext.getLayoutSlots().add(layoutSlot.withBuilderFactory($ -> builder));
        return builder;
    }

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * <pre>{@code
     * layoutSlot('F', (index, builder) -> builder.withItem(...));
     * }</pre>
     *
     * @param character The layout character target.
     */
    public void layoutSlot(char character, @NotNull BiConsumer<Integer, BUILDER> factory) {
        // TODO More detailed exception message
        final LayoutSlot layoutSlot = renderContext.getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        renderContext.getLayoutSlots().add(layoutSlot.withBuilderFactory(index -> {
            final BUILDER builder = builderFactory.get();
            factory.accept(index, builder);
            return builder;
        }));
    }

    @Override
    public <T extends PlatformComponentBuilder<T, CONTEXT>> void layoutSlot(char character, T builder) {
        final LayoutSlot layoutSlot = renderContext.getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        renderContext
                .getLayoutSlots()
                .add(layoutSlot.withComponentFactory(index -> builder.internalBuildComponent(root)));
    }

    private <B extends PlatformComponentBuilder<B, CONTEXT>> void internalSlotComponent(int position, B builder) {
        final Component component =
                builder.withSlot(position).withSelfManaged(true).internalBuildComponent(root);
        renderContext.addComponent(component);
    }

    @Override
    public <T extends PlatformComponentBuilder<T, CONTEXT>> void unsetSlotComponent(T componentBuilder) {
        internalSlotComponent(-1, componentBuilder);
    }
    // endregion

    // region Internals
    /**
     * Throws an {@link IllegalStateException} if container type is not aligned.
     */
    private void checkAlignedContainerTypeForSlotAssignment() {
        if (!renderContext.getContainer().getType().isAligned())
            throw new IllegalStateException(String.format(
                    "Non-aligned container type %s cannot use row-column slots, use absolute %s instead",
                    renderContext.getContainer().getType().getIdentifier(), "#slot(n)"));
    }
    // endregion
}
