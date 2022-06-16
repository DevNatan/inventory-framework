package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@ToString
@Setter(AccessLevel.PACKAGE)
@Getter
public final class ViewItem {

	enum State {UNDEFINED, HOLDING}

	private int slot;
	private State state = State.UNDEFINED;
	private boolean paginationItem;

	@Nullable private String referenceKey;

	private Object item;
	private boolean closeOnClick, cancelOnClick, cancelOnShiftClick;

	// TODO add move in, move out, item hold and item release handlers
	@Getter(AccessLevel.PACKAGE)
	private ViewItemHandler renderHandler, updateHandler, clickHandler;

	@Getter(AccessLevel.NONE)
	private Map<String, Object> data;

	/**
	 * @deprecated You cannot instantiate a ViewItem, use {@link AbstractView#item()} instead.
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
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
	Object getItem() {
		return item;
	}

	@Contract(value = "_ -> this", mutates = "this")
	ViewItem withItem(Object item) {
		setItem(item);
		return this;
	}

	/**
	 * Sets the handler that'll be called when the item is rendered.
	 *
	 * @param renderHandler The render handler.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onRender(@Nullable ViewItemHandler renderHandler) {
		this.renderHandler = renderHandler;
		return this;
	}

	/**
	 * Sets the handler that'll be called when the item is updated.
	 *
	 * @param updateHandler The update handler.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onUpdate(@Nullable ViewItemHandler updateHandler) {
		this.updateHandler = updateHandler;
		return this;
	}

	/**
	 * Sets the handler that'll be called when the item is clicked by a player.
	 *
	 * @param clickHandler The click handler.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onClick(@Nullable ViewItemHandler clickHandler) {
		this.clickHandler = clickHandler;
		return this;
	}

	@Contract(mutates = "this")
	public ViewItem cancelOnClick() {
		return withCancelOnClick(!cancelOnClick);
	}

	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem withCancelOnClick(boolean cancelOnClick) {
		this.cancelOnClick = cancelOnClick;
		return this;
	}

	@Contract(mutates = "this")
	public ViewItem closeOnClick() {
		return withCloseOnClick(!closeOnClick);
	}

	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem withCloseOnClick(boolean closeOnClick) {
		this.closeOnClick = !closeOnClick;
		return this;
	}

	@SuppressWarnings("unchecked")
	<T> T getData(@NotNull String key) {
		return data == null ? null : (T) data.get(key);
	}

	void setData(@NotNull String key, @NotNull Object value) {
		withData(key, value);
	}

	@NotNull
	public ViewItem withData(@NotNull String key, @NotNull Object value) {
		if (data == null)
			data = new HashMap<>();

		data.put(key, value);
		return this;
	}

	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem referencedBy(@NotNull String key) {
		this.referenceKey = key;
		return this;
	}

}