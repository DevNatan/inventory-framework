package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class PlatformItemComponentBuilder<SELF, CONTEXT extends IFContext>
	extends PlatformComponentBuilder<SELF, CONTEXT>
	implements ItemComponentBuilder {

	private int position;

	protected final int getPosition() {
		return position;
	}

	protected final void setPosition(int position) {
		this.position = position;
	}

	public final SELF withSlot(int slot) {
		setPosition(slot);
		return (SELF) this;
	}

	public abstract SELF withSlot(int row, int column); //{
		// FIXME Missing root availability, root must be available
		// final ViewContainer container = ((IFRenderContext) root).getContainer();
		// return withSlot(SlotConverter.convertSlot(row, column, container.getRowsCount(),
		// container.getColumnsCount()));
		// return (SELF) this;
	// }


	@Override
	public final boolean isContainedWithin(int position) {
		return getPosition() == position;
	}

	@Override
	public String toString() {
		return "PlatformItemComponentBuilder{" +
			"position=" + position +
			"} " + super.toString();
	}
}
