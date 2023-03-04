package me.devnatan.inventoryframework.component;

import lombok.AccessLevel;
import lombok.Getter;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.context.SlotContext;
import me.devnatan.inventoryframework.context.SlotRenderContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Getter(AccessLevel.PACKAGE)
public final class BukkitItemComponentBuilder
	extends DefaultComponentBuilder<BukkitItemComponentBuilder>
	implements ItemComponentBuilder<BukkitItemComponentBuilder> {

	private int slot;
	private ItemStack item;

	// --- Handlers ---
	private Consumer<? super SlotRenderContext> renderHandler;
	private Consumer<? super SlotClickContext> clickHandler;
	private Consumer<? super SlotContext> updateHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BukkitItemComponentBuilder withSlot(int slot) {
		this.slot = slot;
		return this;
	}

	/**
	 * Defines the item that will be used as fallback for rendering in the slot where this item is
	 * positioned. The fallback item is always static.
	 *
	 * @param item The new fallback item stack.
	 * @return This item builder.
	 */
	public BukkitItemComponentBuilder withItem(@Nullable ItemStack item) {
		this.item = item;
		return this;
	}

	/**
	 * Called when the item is rendered.
	 * <p>
	 * This handler is called every time the item or the view that owns it is updated.
	 * <p>
	 * It is allowed to change the item that will be displayed in this handler using {@link SlotRenderContext#setItem(ItemStack)}.
	 *
	 * @param renderHandler The render handler.
	 * @return This item builder.
	 */
	public BukkitItemComponentBuilder onRender(@Nullable Consumer<? super SlotRenderContext> renderHandler) {
		this.renderHandler = renderHandler;
		return this;
	}

	/**
	 * Called when a player clicks on the item.
	 * <p>
	 * This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 *
	 * @param clickHandler The click handler.
	 * @return This item builder.
	 */
	public BukkitItemComponentBuilder onClick(@Nullable Consumer<? super SlotClickContext> clickHandler) {
		this.clickHandler = clickHandler;
		return this;
	}

	/**
	 * Called when the item is updated.
	 *
	 * @param updateHandler The update handler.
	 * @return This item builder.
	 */
	public BukkitItemComponentBuilder onUpdate(@Nullable Consumer<? super SlotContext> updateHandler) {
		this.updateHandler = updateHandler;
		return this;
	}
}
