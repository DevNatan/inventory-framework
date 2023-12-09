package me.devnatan.inventoryframework.context;

import static me.devnatan.inventoryframework.utils.SlotConverter.convertSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.UpdateReason;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ComponentHandle;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.PlatformComponent;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

@SuppressWarnings("rawtypes")
public abstract class PlatformRenderContext<ITEM_BUILDER extends ItemComponentBuilder, CONTEXT extends IFContext>
        extends PlatformConfinedContext implements IFRenderContext {

    private final UUID id;
    protected final PlatformView root;
    private final ViewConfig config;
    private final Map<String, Viewer> viewers;
    private Object initialData;
    private final Viewer subject;

    // --- Inherited ---
    private final ViewContainer container;
    private boolean rendered;

    // --- Properties ---
    private final List<ComponentBuilder> componentBuilders = new ArrayList<>();
    private final List<LayoutSlot> layoutSlots = new ArrayList<>();
    private final List<BiFunction<Integer, Integer, ComponentBuilder>> availableSlotFactories = new ArrayList<>();

    PlatformRenderContext(
            @NotNull UUID id,
            @NotNull PlatformView root,
            @NotNull ViewConfig config,
            @NotNull ViewContainer container,
            @NotNull Map<String, Viewer> viewers,
            Viewer subject,
            Object initialData) {
        this.id = id;
        this.root = root;
        this.config = config;
        this.container = container;
        this.viewers = viewers;
        this.subject = subject;
        this.initialData = initialData;
    }

    // region Slot Assignment Methods
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
    public final ITEM_BUILDER unsetSlot() {
        return createRegisteredBuilder();
    }

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public final @NotNull ITEM_BUILDER slot(int slot) {
        return createRegisteredBuilderInPosition(slot);
    }

    /**
     * Adds an item at the specific column and ROW (X, Y) in that context's container.
     *
     * @param row    The row (Y) in which the item will be positioned.
     * @param column The column (X) in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    @NotNull
    public final ITEM_BUILDER slot(int row, int column) {
        checkAlignedContainerTypeForSlotAssignment();
        return createRegisteredBuilderInPosition(convertSlot(
                row, column, getContainer().getRowsCount(), getContainer().getColumnsCount()));
    }

    /**
     * Sets an item in the first slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public final @NotNull ITEM_BUILDER firstSlot() {
        return createRegisteredBuilderInPosition(getContainer().getFirstSlot());
    }

    /**
     * Sets a component in the first slot of the container.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @return An {@link ComponentBuilder} to configure the properties of the component.
     */
    @ApiStatus.Experimental
    public final @NotNull <C extends ComponentHandle<CONTEXT, B>, B extends PlatformComponentBuilder<?, CONTEXT>>
            B firstSlot(@NotNull C component) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets an item in the last slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public final @NotNull ITEM_BUILDER lastSlot() {
        return createRegisteredBuilderInPosition(getContainer().getLastSlot());
    }

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public final @NotNull ITEM_BUILDER availableSlot() {
        final ITEM_BUILDER builder = createBuilder();
        availableSlotFactories.add((index, slot) -> {
            builder.setPosition(slot);
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
    public final void availableSlot(@NotNull BiConsumer<Integer, ITEM_BUILDER> factory) {
        availableSlotFactories.add((index, slot) -> {
            final ITEM_BUILDER builder = createBuilder();
            builder.setPosition(slot);
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
    public final @NotNull ITEM_BUILDER layoutSlot(char character) {
        // TODO More detailed exception message
        final LayoutSlot layoutSlot = getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        final ITEM_BUILDER builder = createBuilder();
        getLayoutSlots().add(layoutSlot.withFactory($ -> (ComponentFactory) builder));
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
    public final void layoutSlot(char character, @NotNull BiConsumer<Integer, ITEM_BUILDER> factory) {
        // TODO More detailed exception message
        final LayoutSlot layoutSlot = getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        getLayoutSlots().add(layoutSlot.withFactory(index -> {
            final ITEM_BUILDER builder = createBuilder();
            factory.accept(index, builder);
            return (ComponentFactory) builder;
        }));
    }

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * @param character The layout character target.
     * @return An item builder to configure the item.
     */
    public @NotNull <BUILDER extends ComponentBuilder, COMPONENT extends PlatformComponent<CONTEXT, BUILDER>>
            BUILDER layoutComponent(char character, COMPONENT component) {

        // TODO More detailed exception message
        final LayoutSlot layoutSlot = getLayoutSlots().stream()
                .filter(value -> value.getCharacter() == character)
                .findFirst()
                .orElseThrow(() -> new InventoryFrameworkException("Missing layout character: " + character));

        // FIXME Missing implementation
        final BUILDER builder = null;
        // final BUILDER builder = component.createBuilder();

        getLayoutSlots().add(layoutSlot.withFactory($ -> (ComponentFactory) builder));
        return builder;
    }

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public final @NotNull ITEM_BUILDER resultSlot() {
        final ViewType containerType = getContainer().getType();
        final int[] resultSlots = containerType.getResultSlots();
        if (resultSlots == null) throw new InventoryFrameworkException("No result slots available: " + containerType);

        if (resultSlots.length > 1)
            throw new InventoryFrameworkException("#resultSlot() do not support types with more than one result slot.");

        return slot(resultSlots[0]);
    }

    /**
     * Renders a new component in that context.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param component The component to be rendered.
     */
    @ApiStatus.Experimental
    public void component(@NotNull Component component) {}
    // endregion

    @Override
    public final @NotNull UUID getId() {
        return id;
    }

    @Override
    public final @NotNull ViewContainer getContainer() {
        return container;
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return config;
    }

    @Override
    public final @NotNull Map<String, Viewer> getIndexedViewers() {
        return viewers;
    }

    @Override
    public final Object getInitialData() {
        return initialData;
    }

    @Override
    public void setInitialData(Object initialData) {
        this.initialData = initialData;
    }

    @Override
    public final Viewer getViewer() {
        return subject;
    }

    @Override
    public final @NotNull @UnmodifiableView List<ComponentBuilder> getNotRenderedComponents() {
        return Collections.unmodifiableList(componentBuilders);
    }

    @Override
    public final @NotNull List<LayoutSlot> getLayoutSlots() {
        return layoutSlots;
    }

    @Override
    public final void addLayoutSlot(@NotNull LayoutSlot layoutSlot) {
        layoutSlots.add(layoutSlot);
    }

    @Override
    public final List<BiFunction<Integer, Integer, ComponentBuilder>> getAvailableSlotFactories() {
        return availableSlotFactories;
    }

    @Override
    public final void closeForPlayer() {
        tryThrowDoNotWorkWithSharedContext("closeForEveryone()");
        super.closeForPlayer();
    }

    @Override
    public final void openForPlayer(@NotNull Class<? extends RootView> other) {
        tryThrowDoNotWorkWithSharedContext("openForEveryone(Class)");
        super.openForPlayer(other);
    }

    @Override
    public final void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData) {
        tryThrowDoNotWorkWithSharedContext("openForEveryone(Class, Object)");
        super.openForPlayer(other, initialData);
    }

    @Override
    public final void updateTitleForPlayer(@NotNull String title) {
        tryThrowDoNotWorkWithSharedContext("updateTitleForEveryone(String)");
        super.updateTitleForEveryone(title);
    }

    @Override
    public final void resetTitleForPlayer() {
        tryThrowDoNotWorkWithSharedContext("resetTitleForEveryone()");
        super.resetTitleForPlayer();
    }

    @Override
    public final boolean isRendered() {
        return rendered;
    }

    // region Internals
    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public final void setRendered() {
        this.rendered = true;
    }

    @Override
    public final void renderComponent(@NotNull Component component) {
        if (!component.shouldRender(this)) {
            component.setVisible(false);

            final Optional<Component> overlapOptional = getOverlappingComponentToRender(this, component);
            if (overlapOptional.isPresent()) {
                Component overlap = overlapOptional.get();
                renderComponent(overlap);

                if (overlap.isVisible()) return;
            }

            component.cleared(this);
            clearComponent(component);
            return;
        }

        component.render(createComponentRenderContext(component, false));
    }

    @Override
    public final void updateComponent(Component component, boolean force, UpdateReason reason) {
        component.updated(createComponentUpdateContext(component, force, reason));
    }

    @Override
    public final void clearComponent(@NotNull Component component) {}

    /**
     * Creates a IFComponentRenderContext for the current platform.
     *
     * @param component The component.
     * @param force If the context was created due to usage of forceRender().
     * @return A new IFComponentRenderContext instance.
     */
    @ApiStatus.Internal
    abstract IFComponentRenderContext createComponentRenderContext(Component component, boolean force);

    /**
     * Creates a IFComponentUpdateContext for the current platform.
     *
     * @param component The component.
     * @param force If the context was created due to usage of forceUpdate().
     * @param reason Reason why this component was updated.
     * @return A new IFComponentUpdateContext instance.
     */
    @ApiStatus.Internal
    abstract IFComponentUpdateContext createComponentUpdateContext(
            Component component, boolean force, UpdateReason reason);

    /**
     * Creates a new platform builder instance.
     *
     * @return A new platform builder instance.
     */
    // TODO use ElementFactory's `createBuilder` instead
    protected abstract ITEM_BUILDER createBuilder();

    /**
     * Creates a new platform builder instance and registers it.
     *
     * @return A new registered platform builder instance.
     */
    protected final ITEM_BUILDER createRegisteredBuilder() {
        final ITEM_BUILDER builder = createBuilder();
        componentBuilders.add(builder);
        return builder;
    }

    /**
     * Creates a new platform builder instance and registers it.
     *
     * @return A new registered platform builder instance.
     */
    protected final ITEM_BUILDER createRegisteredBuilderInPosition(int position) {
        final ITEM_BUILDER builder = createBuilder();
        builder.setPosition(position);
        componentBuilders.add(builder);
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
    // endregion
}
