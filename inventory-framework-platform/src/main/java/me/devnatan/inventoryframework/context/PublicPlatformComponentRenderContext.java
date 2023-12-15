package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateWatcher;
import org.jetbrains.annotations.NotNull;

public abstract class PublicPlatformComponentRenderContext<CONTEXT, ITEM_BUILDER extends ItemComponentBuilder, ITEM>
        extends PlatformConfinedContext
        implements IFComponentRenderContext, PublicSlotComponentRenderer<CONTEXT, ITEM_BUILDER, ITEM> {

    private final IFComponentRenderContext componentContext;
    private final PublicSlotComponentRenderer<CONTEXT, ITEM_BUILDER, ITEM> publicSlotComponentRenderer;

    public PublicPlatformComponentRenderContext(IFComponentRenderContext componentContext) {
        this.componentContext = componentContext;
        publicSlotComponentRenderer = new DefaultPublicSlotComponentRenderer<>(
                this, (IFRenderContext) getTopLevelContext(), this::createItemBuilder);
    }

    protected abstract ITEM_BUILDER createItemBuilder();

    @Override
    public final IFContext getTopLevelContext() {
        return componentContext.getComponent().getContext();
    }

    @Override
    public final Component getComponent() {
        return componentContext.getComponent();
    }

    @Override
    public final IFRenderContext getParent() {
        return componentContext.getParent();
    }

    public final IFComponentRenderContext getConfinedContext() {
        return componentContext;
    }

    @Override
    public final ViewContainer getContainer() {
        return componentContext.getContainer();
    }

    @Override
    public final @NotNull UUID getId() {
        return componentContext.getId();
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return componentContext.getConfig();
    }

    @Override
    public final Object getInitialData() {
        return componentContext.getInitialData();
    }

    @Override
    public final void setInitialData(Object initialData) {
        componentContext.setInitialData(initialData);
    }

    @Override
    public final Viewer getViewer() {
        return componentContext.getViewer();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final @NotNull PlatformView getRoot() {
        return (PlatformView) componentContext.getRoot();
    }

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
    public <T extends PlatformComponentBuilder<T, CONTEXT>> void unsetSlotComponent(T componentBuilder) {
        publicSlotComponentRenderer.unsetSlotComponent(componentBuilder);
    }

    @Override
    public Map<Long, StateValue> getStateValues() {
        return componentContext.getStateValues();
    }

    @Override
    public Map<Long, List<StateWatcher>> getStateWatchers() {
        return componentContext.getStateWatchers();
    }

    @Override
    public String toString() {
        return "PublicComponentRenderContext{" + "componentContext="
                + componentContext + ", publicSlotComponentRenderer="
                + publicSlotComponentRenderer + "} "
                + super.toString();
    }
}
