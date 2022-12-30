package me.devnatan.inventoryframework.state;

import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public interface PaginationState<T> extends State<List<T>> {

	<C extends IFContext> void pageSwitched(@NotNull Consumer<C> handler);

}
