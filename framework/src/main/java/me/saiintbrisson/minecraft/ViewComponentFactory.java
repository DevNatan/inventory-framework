package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ViewComponentFactory {

	@Getter(AccessLevel.PROTECTED)
	private final List<Consumer<AbstractView>> modifiers = new ArrayList<>();

	void registerModifier(@NotNull Consumer<AbstractView> modifier) {
		modifiers.add(modifier);
	}

	@NotNull
	public abstract AbstractView createView(
		int rows,
		String title,
		@NotNull ViewType type
	);

	public abstract void setupView(@NotNull AbstractView view);

	@NotNull
	public abstract ViewContainer createContainer(
		@NotNull VirtualView view,
		int size,
		String title,
		ViewType type
	);

	@NotNull
	public abstract Viewer createViewer(Object... parameters);

	@NotNull
	public abstract BaseViewContext createContext(
		@NotNull AbstractView root,
		ViewContainer container,
		Class<? extends ViewContext> backingContext
	);

	@NotNull
	public abstract AbstractViewSlotContext createSlotContext(
		ViewItem item,
		BaseViewContext parent,
		int paginatedItemIndex,
		Object paginatedItemValue
	);

	public abstract Object createItem(
		@Nullable Object stack
	);

	public abstract boolean worksInCurrentPlatform();

}
