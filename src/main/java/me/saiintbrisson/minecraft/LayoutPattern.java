package me.saiintbrisson.minecraft;

import java.util.Stack;
import java.util.function.Supplier;

public final class LayoutPattern {

	private final char character;
	private final Supplier<ViewItem> factory;
	private final Stack<Integer> slots = new Stack<>();

	public LayoutPattern(
		char character,
		Supplier<ViewItem> factory
	) {
		this.character = character;
		this.factory = factory;
	}

	public char getCharacter() {
		return character;
	}

	public Supplier<ViewItem> getFactory() {
		return factory;
	}

	public Stack<Integer> getSlots() {
		return slots;
	}
}
