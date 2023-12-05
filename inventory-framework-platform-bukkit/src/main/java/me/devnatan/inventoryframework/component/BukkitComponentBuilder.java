package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.context.SlotContext;
import me.devnatan.inventoryframework.context.SlotRenderContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BukkitComponentBuilder<SELF> extends PlatformComponentBuilder<SELF, Context> {

	private Consumer<? super SlotClickContext> clickHandler;

	protected BukkitComponentBuilder() {}

	/**
	 * Called when the item is rendered.
	 * <p>
	 * This handler is called every time the item or the view that owns it is updated.
	 *
	 * @param renderHandler The render handler.
	 * @return This component builder.
	 */
	public final SELF onRender(@Nullable Consumer<? super SlotRenderContext> renderHandler) {
		setRenderHandler(renderHandler);
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

	// region Protected ComponentBuilder API
	@SuppressWarnings("unchecked")
	protected final Consumer<? super IFSlotClickContext> getClickHandler() {
		return (Consumer<? super IFSlotClickContext>) clickHandler;
	}

	protected final void setClickHandler(Consumer<? super IFSlotClickContext> clickHandler) {
		this.clickHandler = clickHandler;
	}
	// endregion
}
