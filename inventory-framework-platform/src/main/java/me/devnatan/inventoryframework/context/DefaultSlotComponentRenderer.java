package me.devnatan.inventoryframework.context;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.PlatformComponent;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.utils.SlotConverter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public final class DefaultSlotComponentRenderer<C extends IFContext, B extends PlatformComponentBuilder<B, C>>
        implements SlotComponentRenderer<C, B> {

    private final VirtualView root;
    private final IFRenderContext renderContext;
    private final Supplier<B> builderFactory;

    public DefaultSlotComponentRenderer(VirtualView root, IFRenderContext renderContext, Supplier<B> builderFactory) {
        this.root = root;
        this.renderContext = renderContext;
        this.builderFactory = builderFactory;
    }

    private PlatformComponentBuilder<?, ?> createRegisteredBuilder() {
        final B builder = builderFactory.get();
        renderContext.getNotRenderedComponents().add(builder);
        return builder;
    }

    @Override
    public B unsetSlot() {
        return (B) createRegisteredBuilder();
    }

    @Override
    public B slot(int slot) {
        return (B) createRegisteredBuilder().withSlot(slot);
    }

    @Override
    public B slot(int row, int column) {
        checkAlignedContainerTypeForSlotAssignment();
        return (B) createRegisteredBuilder().withSlot(row, column);
    }

    @Override
    public <T extends PlatformComponentBuilder<T, C>> void slotComponent(int slot, T builder) {
        internalSlotComponent(slot, builder);
    }

    @Override
    public <T extends PlatformComponentBuilder<T, C>> void slotComponent(int row, int column, T builder) {
        internalSlotComponent(
                SlotConverter.convertSlot(
                        row,
                        column,
                        renderContext.getContainer().getRowsCount(),
                        renderContext.getContainer().getColumnsCount()),
                builder);
    }

    @Override
    public B firstSlot() {
        return slot(renderContext.getContainer().getFirstSlot());
    }

    @Override
    public <U extends PlatformComponent<C, ?>> void firstSlot(U component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends PlatformComponentBuilder<T, C>> void firstSlot(T componentBuilder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public B lastSlot() {
        return slot(renderContext.getContainer().getLastSlot());
    }

    @Override
    public <T extends PlatformComponentBuilder<T, C>> void lastSlotComponent(T builder) {
        internalSlotComponent(renderContext.getContainer().getLastSlot(), builder);
    }

    @ApiStatus.Experimental
    public B resultSlot() {
        final ViewType containerType = renderContext.getContainer().getType();
        final int[] resultSlots = containerType.getResultSlots();
        if (resultSlots == null) throw new InventoryFrameworkException("No result slots available: " + containerType);

        if (resultSlots.length > 1)
            throw new InventoryFrameworkException("#resultSlot() do not support types with more than one result slot.");

        return slot(resultSlots[0]);
    }

    @Override
    public B availableSlot() {
        final B builder = builderFactory.get();
        renderContext.getAvailableSlotFactories().add((index, slot) -> {
            ((PlatformComponentBuilder<?, ?>) builder).withSlot(slot);
            return builder;
        });
        return builder;
    }

    @Override
    public void availableSlot(@NotNull BiConsumer<Integer, B> factory) {
        renderContext.getAvailableSlotFactories().add((index, slot) -> {
            final B builder = builderFactory.get();
            ((PlatformComponentBuilder<?, ?>) builder).withSlot(slot);
            factory.accept(index, builder);
            return builder;
        });
    }

    @Override
    public <T extends PlatformComponentBuilder<T, C>> void availableSlotComponent(T builder) {
        throw new UnsupportedOperationException("Missing availableSlotComponent(T) implementation");
    }

    @Override
    public B layoutSlot(char character) {
        // TODO More detailed exception message
        final LayoutSlot layoutSlot = renderContext.getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        final B builder = builderFactory.get();
        renderContext.getLayoutSlots().add(layoutSlot.withBuilderFactory($ -> builder));
        return builder;
    }

    @Override
    public void layoutSlot(char character, @NotNull BiConsumer<Integer, B> factory) {
        // TODO More detailed exception message
        final LayoutSlot layoutSlot = renderContext.getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        renderContext.getLayoutSlots().add(layoutSlot.withBuilderFactory(index -> {
            final B builder = builderFactory.get();
            factory.accept(index, builder);
            return builder;
        }));
    }

    @Override
    public <T extends PlatformComponentBuilder<T, C>> void layoutSlot(char character, T builder) {
        final LayoutSlot layoutSlot = renderContext.getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        renderContext
                .getLayoutSlots()
                .add(layoutSlot.withComponentFactory(index -> builder.internalBuildComponent(root)));
    }

    private <T extends PlatformComponentBuilder<T, C>> void internalSlotComponent(int position, T builder) {
        final Component component =
                builder.withSlot(position).withSelfManaged(true).internalBuildComponent(root);
        renderContext.addComponent(component);
    }

    @Override
    public <T extends PlatformComponentBuilder<T, C>> void unsetSlotComponent(T componentBuilder) {
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
