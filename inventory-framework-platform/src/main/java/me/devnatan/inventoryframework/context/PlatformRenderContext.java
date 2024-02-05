package me.devnatan.inventoryframework.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import lombok.experimental.Delegate;
import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.UpdateReason;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.ComponentContainer;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.pipeline.Pipelined;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public abstract class PlatformRenderContext<BUILDER extends PlatformComponentBuilder<BUILDER, ?>>
        extends PlatformConfinedContext
        implements IFRenderContext, PublicSlotComponentRenderer<BUILDER>, Pipelined {

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

    @Delegate
    private final PublicSlotComponentRenderer<BUILDER> publicSlotComponentRenderer =
            new DefaultPublicSlotComponentRenderer<>(this, this::createComponentBuilder);

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

    @Override
    public void simulateRender() {
        IFDebug.debug("Rendering context %s", getId());
        getPipeline().execute(PipelinePhase.Context.CONTEXT_RENDER, this);
        resolveLayout();
    }

    @Override
    public void resolveLayout() {
        getPipeline().execute(PipelinePhase.Context.CONTEXT_LAYOUT_RESOLUTION, this);
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    abstract BUILDER createComponentBuilder();

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
        synchronized (getComponents()) {
            this.getComponents().add(0, component);
        }
    }

    @Override
    public final void removeComponent(@NotNull Component component) {
        synchronized (this.getComponents()) {
            this.getComponents().remove(component);
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

        component.getPipeline().execute(PipelinePhase.ComponentPhase.COMPONENT_CLICK, clickContext);
    }

    @Override
    public final void updateComponent(Component component, boolean force, UpdateReason reason) {
        component
                .getPipeline()
                .execute(
                        PipelinePhase.ComponentPhase.COMPONENT_UPDATE,
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
            component.getPipeline().execute(PipelinePhase.ComponentPhase.COMPONENT_CLEAR, clearContext);
            return;
        }

        component
                .getPipeline()
                .execute(PipelinePhase.ComponentPhase.COMPONENT_RENDER, createComponentRenderContext(component, false));
    }

    @Override
    public final void clearComponent(@NotNull Component component) {
        component
                .getPipeline()
                .execute(PipelinePhase.ComponentPhase.COMPONENT_CLEAR, createComponentClearContext(component));
    }
    // endregion
}
