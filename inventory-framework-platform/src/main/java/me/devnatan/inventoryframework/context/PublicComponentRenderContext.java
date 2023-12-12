package me.devnatan.inventoryframework.context;

import java.util.UUID;
import java.util.function.BiConsumer;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.ItemComponent;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class PublicComponentRenderContext<CONTEXT, ITEM_BUILDER extends ItemComponentBuilder, ITEM>
        extends PlatformConfinedContext
        implements IFComponentRenderContext, PublicSlotComponentRenderer<CONTEXT, ITEM_BUILDER, ITEM> {

    private final PublicSlotComponentRenderer<CONTEXT, ITEM_BUILDER, ITEM> publicSlotComponentRenderer =
            new DefaultPublicSlotComponentRenderer<>(this, getParent(), this::createItemBuilder);

    @Override
    public IFContext getTopLevelContext() {
        return null;
    }

    @Override
    public ItemComponent getComponent() {
        return null;
    }

    @Override
    public IFRenderContext getParent() {
        return null;
    }

    public IFComponentRenderContext getConfinedContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ViewContainer getContainer() {
        return null;
    }

    @Override
    public @NotNull UUID getId() {
        return null;
    }

    @Override
    public @NotNull ViewConfig getConfig() {
        return null;
    }

    @Override
    public Object getInitialData() {
        return null;
    }

    @Override
    public void setInitialData(Object initialData) {}

    @Override
    public Viewer getViewer() {
        return null;
    }

    @Override
    public @NotNull PlatformView getRoot() {
        return null;
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
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void slot(int slot, T componentBuilder) {
        publicSlotComponentRenderer.slot(slot, componentBuilder);
    }

    @Override
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void slot(int row, int column, T componentBuilder) {
        publicSlotComponentRenderer.slot(row, column, componentBuilder);
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
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void firstSlot(T componentBuilder) {
        publicSlotComponentRenderer.firstSlot(componentBuilder);
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
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void lastSlot(T componentBuilder) {
        publicSlotComponentRenderer.lastSlot(componentBuilder);
    }

    @Override
    public final ITEM_BUILDER availableSlot() {
        return publicSlotComponentRenderer.availableSlot();
    }

    @Override
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void availableSlot(T componentBuilder) {
        publicSlotComponentRenderer.availableSlot(componentBuilder);
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
    public final <T extends PlatformComponentBuilder<T, CONTEXT>> void layoutSlotComponent(
            char character, T componentBuilder) {
        publicSlotComponentRenderer.layoutSlotComponent(character, componentBuilder);
    }

    @Override
    public final ITEM_BUILDER resultSlot() {
        return publicSlotComponentRenderer.resultSlot();
    }

    @Override
    public final ITEM_BUILDER resultSlot(ITEM item) {
        return publicSlotComponentRenderer.resultSlot(item);
    }

    protected abstract ITEM_BUILDER createItemBuilder();
}
