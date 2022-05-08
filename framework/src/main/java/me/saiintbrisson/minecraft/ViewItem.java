package me.saiintbrisson.minecraft;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static me.saiintbrisson.minecraft.View.UNSET_SLOT;

public class ViewItem {

	public enum State {

		UNDEFINED,

		HOLDING,

	}

	private int slot;
	private ItemStack item;

	// fast path handlers
	private boolean closeOnClick, cancelOnClick, isCancelOnShiftClick, overrideCancelOnClick, overrideCancelOnShiftClick;

	private ViewItemHandler clickHandler, renderHandler, updateHandler;

	private Map<String, Object> data;
	private State state = State.UNDEFINED;
	private boolean isPaginationItem;
	private ViewItem lastInternalState;
	private String reference;
	private ViewSlotContext linkedContext;

	/**
	 * @deprecated Use {@link ViewItem(int)} instead.
	 */
	@Deprecated
	public ViewItem() {
		this(UNSET_SLOT);
	}

	public ViewItem(int slot) {
		this.slot = slot;
	}

	ViewItem getLastInternalState() {
		return lastInternalState;
	}

	void setLastInternalState(ViewItem lastInternalState) {
		this.lastInternalState = lastInternalState;
	}

	State getState() {
		return state;
	}

	void setState(State state) {
		this.state = state;
	}

	public int getSlot() {
		return slot;
	}

	void setSlot(int slot) {
		this.slot = slot;
	}

	public ItemStack getItem() {
		return item;
	}

	public ViewItem withItem(ItemStack item) {
		this.item = item;
		return this;
	}

	public ViewItemHandler getClickHandler() {
		return clickHandler;
	}

	public ViewItemHandler getRenderHandler() {
		return renderHandler;
	}

	void setRenderHandler(ViewItemHandler renderHandler) {
		this.renderHandler = renderHandler;
	}

	public ViewItemHandler getUpdateHandler() {
		return updateHandler;
	}

	void setUpdateHandler(ViewItemHandler updateHandler) {
		this.updateHandler = updateHandler;
	}

	public ViewItem onClick(ViewItemHandler clickHandler) {
		this.clickHandler = clickHandler;
		return this;
	}

	public ViewItem onRender(ViewItemHandler renderHandler) {
		this.renderHandler = renderHandler;
		return this;
	}

	public ViewItem onUpdate(ViewItemHandler updateHandler) {
		this.updateHandler = updateHandler;
		return this;
	}

	public boolean isCloseOnClick() {
		return closeOnClick;
	}

	public void setCloseOnClick(boolean closeOnClick) {
		this.closeOnClick = closeOnClick;
		if (this.closeOnClick)
			setCancelOnClick(true);
	}

	public ViewItem closeOnClick() {
		return withCloseOnClick(true);
	}

	public ViewItem withCloseOnClick(boolean closeOnClick) {
		setCloseOnClick(closeOnClick);
		return this;
	}

	public boolean isOverrideCancelOnClick() {
		return overrideCancelOnClick;
	}

	public boolean isCancelOnClick() {
		return cancelOnClick;
	}

	public void setCancelOnClick(boolean cancelOnClick) {
		this.cancelOnClick = cancelOnClick;
		overrideCancelOnClick = true;
	}

	public ViewItem withCancelOnClick(boolean cancelOnClick) {
		setCancelOnClick(cancelOnClick);
		return this;
	}

	public ViewItem cancelOnClick() {
		return withCancelOnClick(true);
	}

	public boolean isCancelOnShiftClick() {
		return isCancelOnShiftClick;
	}

	public boolean isOverrideCancelOnShiftClick() {
		return overrideCancelOnShiftClick;
	}

	public void setCancelOnShiftClick(boolean cancelOnShiftClick) {
		isCancelOnShiftClick = cancelOnShiftClick;
		overrideCancelOnShiftClick = true;
	}

	public ViewItem withCancelOnShiftClick(boolean cancelOnShiftClick) {
		setCancelOnShiftClick(cancelOnShiftClick);
		return this;
	}

	public ViewItem cancelOnShiftClick() {
		return withCancelOnShiftClick(true);
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(String key) {
		return data == null ? null : (T) data.get(key);
	}

	public void setData(String key, Object value) {
		withData(key, value);
	}

	public ViewItem withData(String key, Object value) {
		if (data == null)
			data = new HashMap<>();

		data.put(key, value);
		return this;
	}

	public boolean isPaginationItem() {
		return isPaginationItem;
	}

	void setPaginationItem(boolean paginationItem) {
		isPaginationItem = paginationItem;
	}

	@NotNull
	ViewSlotContext getLinkedContext() {
		if (linkedContext == null)
			throw new IllegalStateException("Item was not yet rendered");

		return linkedContext;
	}

	void setLinkedContext(ViewSlotContext linkedContext) {
		this.linkedContext = linkedContext;
	}

	public String getReference() {
		return reference;
	}

	public ViewItem referencedBy(@Nullable String referenceKey) {
		this.reference = referenceKey;
		return this;
	}

	@Override
	public String toString() {
		return "ViewItem{" +
			"slot=" + slot +
			", item=" + item +
			", closeOnClick=" + closeOnClick +
			", cancelOnClick=" + cancelOnClick +
			", overrideCancelOnClick=" + overrideCancelOnClick +
			", clickHandler=" + clickHandler +
			", renderHandler=" + renderHandler +
			", updateHandler=" + updateHandler +
			", staticData=" + data +
			", isPaginationItem=" + isPaginationItem +
			", cancelOnShiftClick=" + isCancelOnShiftClick +
			", overrideCancelOnShiftClick=" + overrideCancelOnShiftClick +
//			", lastInternalState=" + lastInternalState +
			", reference=" + reference +
			'}';
	}

}
