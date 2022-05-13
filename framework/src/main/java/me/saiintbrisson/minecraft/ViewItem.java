package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@ToString
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public final class ViewItem {

	enum State {UNDEFINED, HOLDING}

	private int slot;

	@Setter(AccessLevel.PUBLIC)
	private Object item;

	private State state = State.UNDEFINED;

	@Getter(AccessLevel.PUBLIC)
	private boolean paginationItem;
	@Getter(AccessLevel.PUBLIC)
	private boolean closeOnClick,
		cancelOnClick,
		isCancelOnShiftClick,
		overrideCancelOnClick,
		overrideCancelOnShiftClick;

	private ViewItemHandler renderHandler, updateHandler, clickHandler;

	/**
	 * @deprecated Use {@link VirtualView#slot(int)} instead.
	 */
	@Deprecated
	public ViewItem() {
		this(-1);
	}

	ViewItem(final int slot) {
		this.slot = slot;
	}

	/**
	 * The fallback item stack that will be rendered if a function that can render is not defined or if
	 * a function that can render does not render an item.
	 *
	 * @return The fallback item stack.
	 */
	public Object getItem() {
		return item;
	}

	/**
	 * Sets the handler that'll be called when the item is rendered.
	 *
	 * @param renderHandler The render handler.
	 */
	public void onRender(@Nullable ViewItemHandler renderHandler) {
		this.renderHandler = renderHandler;
	}

	/**
	 * Sets the handler that'll be called when the item is updated.
	 *
	 * @param updateHandler The update handler.
	 */
	public void onUpdate(@Nullable ViewItemHandler updateHandler) {
		this.updateHandler = updateHandler;
	}

	/**
	 * Sets the handler that'll be called when the item is clicked by a player.
	 *
	 * @param clickHandler The click handler.
	 */
	public void onClick(@Nullable ViewItemHandler clickHandler) {
		this.clickHandler = clickHandler;
	}

}