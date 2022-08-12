package me.saiintbrisson.minecraft;

import java.util.Stack;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public
class LayoutPattern {

    private final char character;
    private final Supplier<ViewItem> factory;
    private final Stack<Integer> slots = new Stack<>();
}
