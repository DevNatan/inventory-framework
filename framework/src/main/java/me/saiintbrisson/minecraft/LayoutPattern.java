package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Stack;
import java.util.function.Supplier;

@Getter
@ToString
@RequiredArgsConstructor
class LayoutPattern {

	private final char character;
	private final Supplier<ViewItem> factory;
	private final Stack<Integer> slots = new Stack<>();

}