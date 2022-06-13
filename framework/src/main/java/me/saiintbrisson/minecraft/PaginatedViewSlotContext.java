package me.saiintbrisson.minecraft;

public interface PaginatedViewSlotContext<T> extends PaginatedVirtualView<T>, ViewSlotContext {

	int getIndex();

	T getValue();

}
