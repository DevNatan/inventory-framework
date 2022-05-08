package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Stack;

public class DelegatedViewContext extends ViewSlotContext {

	protected final ViewContext delegate;

	public DelegatedViewContext(ViewContext delegate) {
		super(delegate.getView(), delegate.getPlayer(), delegate.getInventory(), -1, null,
			delegate instanceof ViewSlotContext ? ((ViewSlotContext) delegate).getClickOrigin() : null);
		this.delegate = delegate;
	}

	public DelegatedViewContext(ViewContext delegate, int slot, ItemStack item) {
		super(delegate.getView(), delegate.getPlayer(), delegate.getInventory(), slot, item,
			delegate instanceof ViewSlotContext ? ((ViewSlotContext) delegate).getClickOrigin() : null);
		this.delegate = delegate;
	}

	public DelegatedViewContext(
		ViewContext delegate,
		int slot,
		ItemStack item,
		InventoryClickEvent fallbackClickOrigin
	) {
		super(delegate.getView(), delegate.getPlayer(), delegate.getInventory(), slot, item,
			delegate instanceof ViewSlotContext ? ((ViewSlotContext) delegate).getClickOrigin() : fallbackClickOrigin);
		this.delegate = delegate;
	}

	ViewContext getDelegate() {
		return delegate;
	}

	@Override
	protected Stack<Integer> getItemsLayer() {
		return delegate.getItemsLayer();
	}

	@Override
	public String[] getLayout() {
		return delegate.getLayout();
	}

	@Override
	boolean isCheckedLayerSignature() {
		return delegate.isCheckedLayerSignature();
	}

	@Override
	void setCheckedLayerSignature(boolean checkedLayerSignature) {
		delegate.setCheckedLayerSignature(checkedLayerSignature);
	}

	@Override
	public boolean isMarkedToClose() {
		return delegate.isMarkedToClose();
	}

	@Override
	public ViewItem[] getItems() {
		return delegate.getItems();
	}

	@Override
	public Map<Integer, Map<String, Object>> slotData() {
		return delegate.slotData();
	}

	@Override
	public Map<String, Object> getSlotData() {
		return delegate.getSlotData(getSlot());
	}

	@Override
	public Map<String, Object> getSlotData(int slot) {
		return delegate.getSlotData(getSlot());
	}

	@Override
	public void update() {
		delegate.update();
	}

	@Override
	public void close() {
		delegate.close();
	}

	@Override
	public void closeNow() {
		delegate.closeNow();
	}

	@Override
	public void cancelAndClose() {
		delegate.cancelAndClose();
	}

	@Override
	public void clear(int slot) {
		delegate.clear(slot);
	}

	@Override
	public <T> PaginatedViewContext<T> paginated() {
		return delegate.paginated();
	}

	@Override
	public boolean isValid() {
		return delegate.isValid();
	}

	@Override
	public final ViewErrorHandler getErrorHandler() {
		return delegate.getErrorHandler();
	}

	@Override
	public final void setErrorHandler(@Nullable ViewErrorHandler errorHandler) {
		delegate.setErrorHandler(errorHandler);
	}

	@Override
	public boolean isPropagateErrors() {
		return delegate.isPropagateErrors();
	}

	@Override
	public void setPropagateErrors(boolean propagateErrors) {
		delegate.setPropagateErrors(propagateErrors);
	}

	@Override
	public String toString() {
		return "DelegatedViewContext{" +
			"cancelled=" + cancelled +
			", delegate=" + delegate +
			"} " + super.toString();
	}

}