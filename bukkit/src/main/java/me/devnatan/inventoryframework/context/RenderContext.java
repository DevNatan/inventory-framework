package me.devnatan.inventoryframework.context;

import static me.devnatan.inventoryframework.utils.SlotConverter.convertSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.internal.ElementFactory;
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

    @Getter(AccessLevel.PRIVATE)
    private final ViewConfigBuilder inheritedConfigBuilder = new ViewConfigBuilder();

    private final List<ComponentBuilder<?>> componentBuilders = new ArrayList<>();

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

    @Override
    public @NotNull UUID getId() {
        return getParent().getId();
    }

    @Override
    public @NotNull StateHost getStateHost() {
        return getParent().getStateHost();
    }

    @Override
    public @NotNull ViewConfigBuilder modifyConfig() {
        return inheritedConfigBuilder;
    }

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder slot(int slot) {
        return createItemBuilder().withSlot(slot);
    }

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder slot(int slot, @Nullable ItemStack item) {
        return createItemBuilder().withSlot(slot).withItem(item);
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
        return createItemBuilder()
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
        return createItemBuilder()
                .withSlot(convertSlot(
                        row,
                        column,
                        getContainer().getRowsCount(),
                        getContainer().getColumnsCount()))
                .withItem(item);
    }

    /**
     * Adds an item to the first slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder firstSlot() {
        return createItemBuilder().withSlot(getContainer().getFirstSlot());
    }

    /**
     * Adds an item to the first slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder lastSlot() {
        return createItemBuilder().withSlot(getContainer().getLastSlot());
    }

    // TODO documentation
    public @NotNull BukkitItemComponentBuilder availableSlot() {
        throw new UnsupportedOperationException("Available slot is not implemented");
    }

    // TODO documentation
    public @NotNull BukkitItemComponentBuilder layoutSlot(String character) {
        throw new UnsupportedOperationException("Layout slot is not implemented");
    }

    /**
     * Creates a BukkitItemBuilder instance and registers it in this context.
     *
     * @return A new registered BukkitItemBuilder instance.
     */
    private BukkitItemComponentBuilder createItemBuilder() {
        final BukkitItemComponentBuilder builder = new BukkitItemComponentBuilder();
        componentBuilders.add(builder);
        return builder;
    }

    @Override
    public @UnmodifiableView @NotNull List<Component> getComponents() {
        final ElementFactory elementFactory = getRoot().getElementFactory();
        return getComponentBuilders().stream()
                .map(elementFactory::buildComponent)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull @UnmodifiableView List<ComponentBuilder<?>> getRegisteredComponentBuilders() {
        return Collections.unmodifiableList(componentBuilders);
    }
}
