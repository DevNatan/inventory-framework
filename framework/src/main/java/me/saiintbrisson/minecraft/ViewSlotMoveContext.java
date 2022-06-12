package me.saiintbrisson.minecraft;

public interface ViewSlotMoveContext extends ViewSlotContext {

	ViewContainer getTargetContainer();

	<T> T getTargetItem();

	int getTargetSlot();

	<T> T getSwappedItem();

	boolean isSwap();

	boolean isStack();

}