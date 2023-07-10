package me.devnatan.inventoryframework.context;

import static java.lang.String.format;
import static me.devnatan.inventoryframework.utils.SlotConverter.convertSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

abstract class PlatformRenderContext<T extends ItemComponentBuilder<T>> extends ConfinedContext
        implements IFRenderContext {

    private final List<ComponentFactory> componentBuilders = new ArrayList<>();
    private final List<LayoutSlot> layoutSlots =
            new ArrayList<>(Collections.singletonList(new LayoutSlot(LayoutSlot.FILLED_RESERVED_CHAR, $ -> {
                throw new IllegalStateException("Cannot use factory of reserved char");
            })));
    private BiFunction<Integer, Integer, ComponentFactory> availableSlotFactory;
    private final ViewConfig config;
    private final UUID id;

    PlatformRenderContext(
            UUID id, RootView root, ViewContainer container, Viewer viewer, ViewConfig config, Object initialData) {
        super(root, container, viewer, initialData);
        this.id = id;
        this.config = config;
    }

    @Override
    public @NotNull UUID getId() {
        return id;
    }

    @Override
    public @NotNull ViewConfig getConfig() {
        return config;
    }

    @Override
    public final @NotNull @UnmodifiableView List<ComponentFactory> getComponentFactories() {
        return Collections.unmodifiableList(componentBuilders);
    }

    @Override
    public final @NotNull @UnmodifiableView List<LayoutSlot> getLayoutSlots() {
        return Collections.unmodifiableList(layoutSlots);
    }

    @Override
    public final void addLayoutSlot(@NotNull LayoutSlot layoutSlot) {
        layoutSlots.add(layoutSlot);
    }

    @Override
    public BiFunction<Integer, Integer, ComponentFactory> getAvailableSlotFactory() {
        return availableSlotFactory;
    }

    // TODO needs documentation
    @ApiStatus.Experimental
    public final T unsetSlot() {
        return createRegisteredBuilder().withSlot();
    }

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public final @NotNull T slot(int slot) {
        return createRegisteredBuilder().withSlot(slot);
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
        return createRegisteredBuilder()
                .withSlot(convertSlot(
                        row,
                        column,
                        getContainer().getRowsCount(),
                        getContainer().getColumnsCount()));
    }

    /**
     * Sets an item in the first slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public @NotNull T firstSlot() {
        return createRegisteredBuilder().withSlot(getContainer().getFirstSlot());
    }

    /**
     * Sets an item in the last slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public @NotNull T lastSlot() {
        return createRegisteredBuilder().withSlot(getContainer().getLastSlot());
    }

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public @NotNull T availableSlot() {
        final T builder = createBuilder();
        availableSlotFactory =
                (index, slot) -> (ComponentFactory) builder.copy().withSlot(slot);
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
        availableSlotFactory = (index, slot) -> {
            final T builder = createBuilder();
            builder.withSlot(slot);
            factory.accept(index, builder);
            return (ComponentFactory) builder;
        };
    }

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * @param character The layout character target.
     * @return An item builder to configure the item.
     */
    public @NotNull T layoutSlot(char character) {
        requireNonReservedLayoutCharacter(character);
        final T builder = createBuilder();
        layoutSlots.add(new LayoutSlot(character, $ -> (ComponentFactory) builder));
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
        requireNonReservedLayoutCharacter(character);
        layoutSlots.add(new LayoutSlot(character, index -> {
            final T builder = createBuilder();
            factory.accept(index, builder);
            return (ComponentFactory) builder;
        }));
    }

    /**
     * Creates a new platform builder instance.
     *
     * @return A new platform builder instance.
     */
    // TODO use ElementFactory's `createBuilder` instead
    protected abstract T createBuilder();

    /**
     * Creates a new platform builder instance and registers it.
     *
     * @return A new registered platform builder instance.
     */
    protected final T createRegisteredBuilder() {
        final T builder = createBuilder();
        componentBuilders.add((ComponentFactory) builder);
        return builder;
    }

    /**
     * Throws an {@link IllegalStateException} if container type is not aligned.
     */
    private void checkAlignedContainerTypeForSlotAssignment() {
        if (!getContainer().getType().isAligned())
            throw new IllegalStateException(String.format(
                    "Non-aligned container type %s cannot use row-column slots, use absolute %s instead",
                    getContainer().getType().getIdentifier(), "#slot(n)"));
    }

    /**
     * Checks if the character is a reserved layout character.
     *
     * @param character The character to be checked.
     * @throws IllegalArgumentException If the given character is a reserved layout character.
     */
    private void requireNonReservedLayoutCharacter(char character) {
        if (character == LayoutSlot.FILLED_RESERVED_CHAR)
            throw new IllegalArgumentException(format(
                    "The '%c' character cannot be used because it is only available for backwards compatibility. Please use another character.",
                    character));
    }
}
