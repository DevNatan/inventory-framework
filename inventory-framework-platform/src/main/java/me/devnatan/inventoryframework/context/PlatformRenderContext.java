package me.devnatan.inventoryframework.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.UpdateReason;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.ComponentContainer;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.pipeline.Pipelined;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public abstract class PlatformRenderContext<CONTEXT, ITEM_BUILDER extends ItemComponentBuilder, ITEM>
        extends PlatformConfinedContext
        implements IFRenderContext, PublicSlotComponentRenderer<CONTEXT, ITEM_BUILDER, ITEM>, Pipelined {

    private final UUID id;
    protected final PlatformView root;
    private final ViewConfig config;
    private final Map<String, Viewer> viewers;
    private Object initialData;
    private final Viewer subject;

    // --- Inherited ---
    private final ViewContainer container;
    private boolean rendered;

    // --- Internal Properties ---
    private final List<ComponentBuilder> componentBuilders = new ArrayList<>();
    private final List<LayoutSlot> layoutSlots = new ArrayList<>();
    private final List<BiFunction<Integer, Integer, ComponentBuilder>> availableSlotFactories = new ArrayList<>();
    private final PublicSlotComponentRenderer<CONTEXT, ITEM_BUILDER, ITEM> publicSlotComponentRenderer =
            new DefaultPublicSlotComponentRenderer<>(this, this, this::createItemBuilder);

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

        final Pipeline<IFContext> pipeline = getPipeline();
        pipeline.intercept(PipelinePhase.Context.CONTEXT_LAYOUT_RESOLUTION, new LayoutResolutionInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_RENDER, new ContextPlatformRenderHandlerCallInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_RENDER, new LayoutRenderInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_RENDER, new AvailableSlotResolutionInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_RENDER, new ContextFirstRenderInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_VIEWER_ADDED, new ScheduledUpdateStartInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_VIEWER_REMOVED, new ScheduledUpdateFinishInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_UPDATE, new ContextPlatformUpdateHandlerCallInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_UPDATE, new ContextUpdateInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_CLOSE, new ContextPlatformCloseHandlerCallInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_CLOSE, new ContextInvalidateInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_SLOT_CLICK, new ViewerLastInteractionTrackerInterceptor());
        pipeline.intercept(PipelinePhase.Context.CONTEXT_SLOT_CLICK, new SlotClickToComponentClickCallInterceptor());
    }

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
    public final @NotNull List<ComponentBuilder> getNotRenderedComponents() {
        return componentBuilders;
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
    public void simulateCloseForPlayer() {
        super.simulateCloseForPlayer();
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
        tryThrowDoNotWorkWithSharedContext("updateTitleForPlayer(String)");
        super.updateTitleForPlayer(title);
    }

    @Override
    public final void resetTitleForPlayer() {
        tryThrowDoNotWorkWithSharedContext("resetTitleForEveryone()");
        super.resetTitleForPlayer();
    }

    // region Internals
    @Override
    public final boolean isRendered() {
        return rendered;
    }

    @Override
    public final void markRendered() {
        this.rendered = true;
    }

    @Override
    public void simulateClick(int rawSlot, Viewer whoClicked, Object platformEvent, boolean isCombined) {
        ViewContainer clickedContainer = getContainer().at(rawSlot);
        if (clickedContainer == null) clickedContainer = whoClicked.getSelfContainer();

        final Component clickedComponent = getComponentsAt(rawSlot).stream()
                .filter(Component::isVisible)
                .findFirst()
                .orElse(null);

        final IFSlotClickContext clickContext = getRoot()
                .getElementFactory()
                .createSlotClickContext(
                        rawSlot, whoClicked, clickedContainer, clickedComponent, platformEvent, isCombined);

        getPipeline().execute(PipelinePhase.Context.CONTEXT_SLOT_CLICK, clickContext);
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    abstract ITEM_BUILDER createItemBuilder();

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
     * Creates a IFComponentClearContext for the current platform.
     *
     * @param component The component.
     * @return A new IFComponentClearContext instance.
     */
    @ApiStatus.Internal
    abstract IFComponentClearContext createComponentClearContext(Component component);
    // endregion

    // region Components API
    @Override
    public final void addComponent(@NotNull Component component) {
        synchronized (getInternalComponents()) {
            getInternalComponents().add(0, component);
        }
    }

    @Override
    public final void removeComponent(@NotNull Component component) {
        synchronized (getInternalComponents()) {
            getInternalComponents().remove(component);
        }
    }

    @Override
    public void clickComponent(
            @NotNull Component component,
            @NotNull Viewer viewer,
            @NotNull ViewContainer clickedContainer,
            Object platformEvent,
            int clickedSlot,
            boolean combined) {
        final RootView root = getRoot();
        final IFSlotClickContext clickContext = root.getElementFactory()
                .createSlotClickContext(clickedSlot, viewer, clickedContainer, component, platformEvent, combined);

        getPipeline().execute(PipelinePhase.Component.COMPONENT_CLICK, clickContext);
    }

    @Override
    public final void updateComponent(Component component, boolean force, UpdateReason reason) {
        getPipeline()
                .execute(
                        PipelinePhase.Component.COMPONENT_UPDATE,
                        createComponentUpdateContext(component, force, reason));
    }

    @Override
    public final void renderComponent(@NotNull Component component) {
        // Custom components can use show()/hide() to change the visibility state of its components
        // and `component.shouldRender(this)` only handles `displayIf`/`hideIf` that is used by
        // regular components by non-custom component developers instead
        final boolean wasVisibilityProgrammaticallySet = component.isSelfManaged() && !component.isVisible();
        if (isRendered() && (!component.shouldRender(this) || wasVisibilityProgrammaticallySet)) {
            component.setVisible(false);

            final Optional<Component> overlapOptional = getOverlappingComponentToRender(
                    component instanceof ComponentContainer ? (ComponentContainer) component : this, component);
            if (overlapOptional.isPresent()) {
                Component overlap = overlapOptional.get();
                renderComponent(overlap);

                if (overlap.isVisible()) return;
            }

            final IFComponentClearContext clearContext = createComponentClearContext(component);
            getPipeline().execute(PipelinePhase.Component.COMPONENT_CLEAR, clearContext);
            return;
        }

        getPipeline().execute(PipelinePhase.Component.COMPONENT_RENDER, createComponentRenderContext(component, false));
    }

    @Override
    public final void clearComponent(@NotNull Component component) {
        getPipeline().execute(PipelinePhase.Component.COMPONENT_CLEAR, createComponentClearContext(component));
    }
    // endregion

    @Override
    public final ITEM_BUILDER unsetSlot() {
        return publicSlotComponentRenderer.unsetSlot();
    }

    @Override
    public final ITEM_BUILDER slot(int slot) {
        return publicSlotComponentRenderer.slot(slot);
    }

    @Override
    public final ITEM_BUILDER slot(int slot, ITEM item) {
        return publicSlotComponentRenderer.slot(slot, item);
    }

    @Override
    public final ITEM_BUILDER slot(int row, int column) {
        return publicSlotComponentRenderer.slot(row, column);
    }

    @Override
    public final ITEM_BUILDER slot(int row, int column, ITEM item) {
        return publicSlotComponentRenderer.slot(row, column, item);
    }

    @Override
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void slotComponent(int slot, T componentBuilder) {
        publicSlotComponentRenderer.slotComponent(slot, componentBuilder);
    }

    @Override
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void slotComponent(
            int row, int column, T componentBuilder) {
        publicSlotComponentRenderer.slotComponent(row, column, componentBuilder);
    }

    @Override
    public final ITEM_BUILDER firstSlot() {
        return publicSlotComponentRenderer.firstSlot();
    }

    @Override
    public final ITEM_BUILDER firstSlot(ITEM item) {
        return publicSlotComponentRenderer.firstSlot(item);
    }

    @Override
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void firstSlotComponent(T componentBuilder) {
        publicSlotComponentRenderer.firstSlotComponent(componentBuilder);
    }

    @Override
    public final ITEM_BUILDER lastSlot() {
        return publicSlotComponentRenderer.lastSlot();
    }

    @Override
    public final ITEM_BUILDER lastSlot(ITEM item) {
        return publicSlotComponentRenderer.lastSlot(item);
    }

    @Override
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void lastSlotComponent(T componentBuilder) {
        publicSlotComponentRenderer.lastSlotComponent(componentBuilder);
    }

    @Override
    public final ITEM_BUILDER availableSlot() {
        return publicSlotComponentRenderer.availableSlot();
    }

    @Override
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void availableSlotComponent(T componentBuilder) {
        publicSlotComponentRenderer.availableSlotComponent(componentBuilder);
    }

    @Override
    public final ITEM_BUILDER availableSlot(ITEM item) {
        return publicSlotComponentRenderer.availableSlot();
    }

    @Override
    public final void availableSlot(@NotNull BiConsumer<Integer, ITEM_BUILDER> factory) {
        publicSlotComponentRenderer.availableSlot(factory);
    }

    @Override
    public final ITEM_BUILDER layoutSlot(char character) {
        return publicSlotComponentRenderer.layoutSlot(character);
    }

    @Override
    public final ITEM_BUILDER layoutSlot(char character, ITEM item) {
        return publicSlotComponentRenderer.layoutSlot(character, item);
    }

    @Override
    public final void layoutSlot(char character, BiConsumer<Integer, ITEM_BUILDER> factory) {
        publicSlotComponentRenderer.layoutSlot(character, factory);
    }

    @Override
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void layoutSlot(char character, T componentBuilder) {
        publicSlotComponentRenderer.layoutSlot(character, componentBuilder);
    }

    @Override
    public final ITEM_BUILDER resultSlot() {
        return publicSlotComponentRenderer.resultSlot();
    }

    @Override
    public final ITEM_BUILDER resultSlot(ITEM item) {
        return publicSlotComponentRenderer.resultSlot(item);
    }

    @Override
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void unsetSlotComponent(T componentBuilder) {
        publicSlotComponentRenderer.unsetSlotComponent(componentBuilder);
    }
}
