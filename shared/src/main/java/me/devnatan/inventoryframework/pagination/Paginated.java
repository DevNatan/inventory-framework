package me.devnatan.inventoryframework.pagination;

import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface Paginated<T> {

	void onPaginatedItemRender(PaginatedViewSlotContext<T> context, ViewItem item, T value);

}
