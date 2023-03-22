package me.devnatan.inventoryframework.context;

import static java.lang.String.format;
import static me.devnatan.inventoryframework.utils.SlotConverter.convertSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import lombok.Getter;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.StateHost;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

@Getter
public final class RenderContext extends ConfinedContext implements IFRenderContext, Context {

    private final @NotNull IFContext parent;
    private final @NotNull Player player;

    private final List<ComponentBuilder<?>> componentBuilders = new ArrayList<>();
    private final List<LayoutSlot> layoutSlots = new ArrayList<>();
    private final List<BiFunction<Integer, Integer, ComponentBuilder<?>>> availableSlots = new ArrayList<>();

    @ApiStatus.Internal
    public RenderContext(
            @NotNull RootView root,
            @NotNull ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull IFContext parent) {
        super(root, container, viewer);
        this.player = ((BukkitViewer) viewer).getPlayer();
        this.parent = parent;
    }

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder slot(int slot) {
        return createRegisteredComponentBuilder().withSlot(slot);
    }

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder slot(int slot, @Nullable ItemStack item) {
        return createRegisteredComponentBuilder().withSlot(slot).withItem(item);
    }

    /**
     * Adds an item at the specific column and ROW (X, Y) in that context's container.
     *
     * @param row    The row (Y) in which the item will be positioned.
     * @param column The column (X) in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    @NotNull
    public BukkitItemComponentBuilder slot(int row, int column) {
        return createRegisteredComponentBuilder()
                .withSlot(convertSlot(
                        row,
                        column,
                        getContainer().getRowsCount(),
                        getContainer().getColumnsCount()));
    }

    /**
     * Adds an item at the specific column and ROW (X, Y) in that context's container.
     *
     * @param row    The row (Y) in which the item will be positioned.
     * @param column The column (X) in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    @NotNull
    public BukkitItemComponentBuilder slot(int row, int column, @Nullable ItemStack item) {
        return createRegisteredComponentBuilder()
                .withSlot(convertSlot(
                        row,
                        column,
                        getContainer().getRowsCount(),
                        getContainer().getColumnsCount()))
                .withItem(item);
    }

    /**
     * Sets an item in the first slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder firstSlot() {
        return createRegisteredComponentBuilder().withSlot(getContainer().getFirstSlot());
    }

    /**
     * Sets an item in the first slot of this context's container.
     *
     * @param item The item that'll be set.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder firstSlot(@Nullable ItemStack item) {
        return createRegisteredComponentBuilder()
                .withSlot(getContainer().getFirstSlot())
                .withItem(item);
    }

    /**
     * Sets an item in the last slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder lastSlot() {
        return createRegisteredComponentBuilder().withSlot(getContainer().getLastSlot());
    }

    /**
     * Sets an item in the last slot of this context's container.
     *
     * @param item The item that'll be set.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder lastSlot(@Nullable ItemStack item) {
        return createRegisteredComponentBuilder()
                .withSlot(getContainer().getLastSlot())
                .withItem(item);
    }

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder availableSlot() {
        final BukkitItemComponentBuilder builder = new BukkitItemComponentBuilder();
        availableSlots.add(($, $$) -> builder);
        return builder;
    }

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * @param item The item that'll be added.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder availableSlot(@Nullable ItemStack item) {
        return availableSlot().withItem(item);
    }

    // TODO documentation
    public void availableSlot(@NotNull BiConsumer<Integer, BukkitItemComponentBuilder> factory) {
        final BukkitItemComponentBuilder builder = new BukkitItemComponentBuilder();
        availableSlots.add((index, slot) -> {
            builder.withSlot(slot);
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
    public @NotNull BukkitItemComponentBuilder layoutSlot(char character) {
        requireNonReservedLayoutCharacter(character);

        final BukkitItemComponentBuilder builder = new BukkitItemComponentBuilder();
        layoutSlots.add(new LayoutSlot(character, $ -> builder));
        return builder;
    }

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * @param character The layout character target.
     * @param item      The item that'll represent the layout character.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder layoutSlot(char character, @Nullable ItemStack item) {
        requireNonReservedLayoutCharacter(character);

        final BukkitItemComponentBuilder builder = new BukkitItemComponentBuilder().withItem(item);
        layoutSlots.add(new LayoutSlot(character, $ -> builder));
        return builder;
    }

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * @param character The layout character target.
     */
    public void layoutSlot(char character, @NotNull BiConsumer<Integer, BukkitItemComponentBuilder> factory) {
        requireNonReservedLayoutCharacter(character);

        layoutSlots.add(new LayoutSlot(character, index -> {
            final BukkitItemComponentBuilder builder = new BukkitItemComponentBuilder();
            factory.accept(index, builder);
            return builder;
        }));
    }

    private void requireNonReservedLayoutCharacter(char character) {
        if (character == LayoutSlot.FILLED_RESERVED_CHAR)
            throw new IllegalArgumentException(format(
                    "The '%c' character cannot be used because it is only available for backwards compatibility. Please use another character.",
                    character));
    }

    /**
     * Creates a BukkitItemBuilder instance and registers it in this context.
     *
     * @return A new registered BukkitItemBuilder instance.
     */
    private BukkitItemComponentBuilder createRegisteredComponentBuilder() {
        final BukkitItemComponentBuilder builder = new BukkitItemComponentBuilder();
        componentBuilders.add(builder);
        return builder;
    }

    @Override
    public @NotNull UUID getId() {
        return getParent().getId();
    }

    @Override
    public @NotNull StateHost getStateHost() {
        return getParent().getStateHost();
    }

    @Override
    public @NotNull @UnmodifiableView List<ComponentBuilder<?>> getRegisteredComponentBuilders() {
        return Collections.unmodifiableList(componentBuilders);
    }

    @Override
    public @NotNull @UnmodifiableView List<LayoutSlot> getLayoutSlots() {
        return Collections.unmodifiableList(layoutSlots);
    }

    @Override
    public @NotNull @UnmodifiableView List<BiFunction<Integer, Integer, ComponentBuilder<?>>> getAvailableSlots() {
        return Collections.unmodifiableList(availableSlots);
    }
}
