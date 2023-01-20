package me.devnatan.inventoryframework.internal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.devnatan.inventoryframework.IFItem;

import java.util.Stack;
import java.util.function.Function;

@Data
public final class LayoutPattern {

	private final char character;

	/**
	 * All slots where this layout pattern is defined.
	 */
	@EqualsAndHashCode.Exclude
	private final Stack<Integer> slots = new Stack<>();

	/**
	 * The first parameter is the current iteration index.
	 */
	@EqualsAndHashCode.Exclude
	private final Function<Integer, IFItem> factory;

}
