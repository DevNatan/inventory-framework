package me.devnatan.inventoryframework.component;

import java.util.function.Consumer;
import lombok.Data;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import org.jetbrains.annotations.NotNull;

@Data
public final class ItemComponent implements Component, InteractionHandler {

    private final int position;
    private final Object stack;

    // --- Handlers ---
    private final Consumer<? super IFSlotRenderContext> renderHandler;
    private final Consumer<? super IFSlotContext> updateHandler;

    @Override
    public @NotNull VirtualView getRoot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isContainedWithin(int position) {
        return getPosition() == position;
    }

    @Override
    public @NotNull InteractionHandler getInteractionHandler() {
        return this;
    }

    @Override
    public void render(@NotNull IFSlotRenderContext context) {
        if (renderHandler != null) {
            if (stack == null)
                throw new IllegalStateException("At least one fallback item or item render handler must be provided.");

            renderHandler.accept(context);
            context.getContainer().renderItem(getPosition(), context.getResult());
        }

        context.getContainer().renderItem(getPosition(), stack);
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {
        if (updateHandler == null) return;
        updateHandler.accept(context);
    }

    @Override
    public void clear(@NotNull IFContext context) {
        context.getContainer().removeItem(getPosition());
    }

    @Override
    public void clicked(@NotNull Component component, @NotNull IFSlotClickContext context) {}
}
