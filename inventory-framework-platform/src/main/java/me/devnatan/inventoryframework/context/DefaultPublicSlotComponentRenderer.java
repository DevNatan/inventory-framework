package me.devnatan.inventoryframework.context;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public final class DefaultPublicSlotComponentRenderer<T extends PlatformComponentBuilder<T, ?>>
        implements PublicSlotComponentRenderer<T> {

    private final IFRenderContext renderContext;
    private final Supplier<T> builderFactory;

    public DefaultPublicSlotComponentRenderer(IFRenderContext renderContext, Supplier<T> builderFactory) {
        this.renderContext = renderContext;
        this.builderFactory = builderFactory;
    }

    private PlatformComponentBuilder<?, ?> createRegisteredBuilder() {
        final T builder = builderFactory.get();
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
    public T unsetSlot() {
        return (T) createRegisteredBuilder();
    }

	@Override
	public void component(Component component) {

	}

	/**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public T slot(int slot) {
        return (T) createRegisteredBuilder().withSlot(slot);
    }

    /**
     * Adds an item at the specific column and ROW (X, Y) in that context's container.
     *
     * @param row    The row (Y) in which the item will be positioned.
     * @param column The column (X) in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    @NotNull
    public T slot(int row, int column) {
        checkAlignedContainerTypeForSlotAssignment();
        return (T) createRegisteredBuilder().withSlot(row, column);
    }

    /**
     * Sets an item in the first slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public T firstSlot() {
        return slot(renderContext.getContainer().getFirstSlot());
    }

    /**
     * Sets an item in the last slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public T lastSlot() {
        return slot(renderContext.getContainer().getLastSlot());
    }

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public T resultSlot() {
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
    public T availableSlot() {
        final T builder = builderFactory.get();
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
    public void availableSlot(@NotNull BiConsumer<Integer, T> factory) {
        renderContext.getAvailableSlotFactories().add((index, slot) -> {
            final T builder = builderFactory.get();
            ((PlatformComponentBuilder<?, ?>) builder).withSlot(slot);
            factory.accept(index, builder);
            return builder;
        });
    }

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * @param character The layout character target.
     * @return An item builder to configure the item.
     */
    public T layoutSlot(char character) {
        // TODO More detailed exception message
        final LayoutSlot layoutSlot = renderContext.getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        final T builder = builderFactory.get();
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
    public void layoutSlot(char character, @NotNull BiConsumer<Integer, T> factory) {
        // TODO More detailed exception message
        final LayoutSlot layoutSlot = renderContext.getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        renderContext.getLayoutSlots().add(layoutSlot.withBuilderFactory(index -> {
            final T builder = builderFactory.get();
            factory.accept(index, builder);
            return builder;
        }));
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
