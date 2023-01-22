package me.devnatan.inventoryframework;

import lombok.Getter;
import me.devnatan.inventoryframework.context.IFSlotContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public final class BukkitItem extends IFItem<BukkitItem> {

	private Consumer<ViewSlotContext> renderHandler, updateHandler;
	private Consumer<ViewSlotClickContext> clickHandler;
	private Consumer<ViewSlotClickContext> holdHandler;
	private BiConsumer<ViewSlotClickContext, ViewSlotClickContext> releaseHandler;

	/**
	 * Called when the item is rendered.
	 *
	 * <p>This handler is called every time the item or the view that owns it is updated.
	 *
	 * <p>It is allowed to change the item that will be displayed in this handler using the context
	 * mutation functions, e.g.: {@link IFSlotContext#setItem(Object)}.
	 *
	 * <p>An item can be re-rendered individually using {@link IFSlotContext#updateSlot()}.
	 *
	 * @param renderHandler The render handler.
	 * @return This item.
	 */
	public BukkitItem onRender(@Nullable Consumer<ViewSlotContext> renderHandler) {
		this.renderHandler = renderHandler;
		return this;
	}

	/**
	 * Called when the item is updated.
	 *
	 * <p>It is allowed to change the item that will be displayed in this handler using the context
	 * mutation functions, e.g.: {@link IFSlotContext#setItem(Object)}.
	 *
	 * <p>An item can be updated individually using {@link IFSlotContext#updateSlot()}.
	 *
	 * @param updateHandler The update handler.
	 * @return This item.
	 */
	public BukkitItem onUpdate(@Nullable Consumer<ViewSlotContext> updateHandler) {
		this.updateHandler = updateHandler;
		return this;
	}

	/**
	 * Called when a player clicks on the item.
	 *
	 * <p>This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 *
	 * <p>**Using item mutation functions in this handler is not allowed.**
	 *
	 * @param clickHandler The click handler.
	 * @return This item.
	 */
	public BukkitItem onClick(@Nullable Consumer<ViewSlotClickContext> clickHandler) {
		this.clickHandler = clickHandler;
		return this;
	}

	/**
	 * Called when a player holds an item.
	 *
	 * <p>This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 *
	 * <p>You can check if the item has been released using {@link #onRelease(BiConsumer)}.
	 *
	 * <p>**Using item mutation functions in this handler is not allowed.**
	 *
	 * @param holdHandler The item hold handler.
	 * @return This item.
	 */
	public BukkitItem onHold(@Nullable Consumer<ViewSlotClickContext> holdHandler) {
		this.holdHandler = holdHandler;
		return this;
	}

	/**
	 * Called when a player releases an item.
	 *
	 * <p>This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 *
	 * <p>You can know when the item was hold using {@link #onHold(Consumer)}.
	 *
	 * <p>**Using item mutation functions in this handler is not allowed.**
	 *
	 * @param releaseHandler The item release handler.
	 * @return This item.
	 */
	public BukkitItem onRelease(@Nullable BiConsumer<ViewSlotClickContext, ViewSlotClickContext> releaseHandler) {
		this.releaseHandler = releaseHandler;
		return this;
	}
}
