package me.devnatan.inventoryframework.component;

import java.util.function.Consumer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.context.SlotRenderContext;
import org.jetbrains.annotations.Nullable;

public abstract class BukkitComponentBuilder<SELF> extends PlatformComponentBuilder<SELF, Context> {

    protected BukkitComponentBuilder() {}

    /**
     * Called when the item is rendered.
     * <p>
     * This handler is called every time the item or the view that owns it is updated.
     *
     * @param renderHandler The render handler.
     * @return This component builder.
     */
    @SuppressWarnings("unchecked")
    public final SELF onRender(@Nullable Consumer<? super SlotRenderContext> renderHandler) {
        setRenderHandler((Consumer<? super IFComponentRenderContext>) renderHandler);
        return (SELF) this;
    }

    /**
     * Called when a player clicks on the component.
     * <p>
     * This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * @param clickHandler The click handler.
     * @return This item builder.
     */
    @SuppressWarnings("unchecked")
    public final SELF onClick(@Nullable Consumer<? super SlotClickContext> clickHandler) {
        setClickHandler((Consumer<? super IFSlotClickContext>) clickHandler);
        return (SELF) this;
    }

    /** {@inheritDoc} */
    @Override
    public abstract Component build(VirtualView root);
}
