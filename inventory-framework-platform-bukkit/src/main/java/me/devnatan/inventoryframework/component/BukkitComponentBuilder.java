package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.context.SlotContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BukkitComponentBuilder<SELF extends ComponentBuilder<SELF>> extends PlatformComponentBuilder<SELF, Context> {

	private Consumer<? super SlotClickContext> clickHandler;

	protected BukkitComponentBuilder() {}

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
        setClickHandler(clickHandler);
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
    public final SELF onClick(@Nullable Runnable clickHandler) {
        return onClick(clickHandler == null ? null : $ -> clickHandler.run());
    }

	/** {@inheritDoc} */
    @Override
    public abstract Component build(VirtualView root);

	// region Protected ComponentBuilder API
	protected final Consumer<? super SlotClickContext> getClickHandler() {
		return clickHandler;
	}

	protected final void setClickHandler(Consumer<? super SlotClickContext> clickHandler) {
		this.clickHandler = clickHandler;
	}
	// endregion
}
