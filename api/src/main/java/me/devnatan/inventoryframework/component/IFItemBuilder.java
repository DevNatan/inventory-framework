package me.devnatan.inventoryframework.component;

public interface IFItemBuilder<S extends IFItemBuilder<S>> extends ComponentBuilder<S> {

	/**
	 * Sets the slot that the item will be positioned.
	 *
	 * @param slot The item slot.
	 * @return This item builder.
	 */
	S withSlot(int slot);

}
