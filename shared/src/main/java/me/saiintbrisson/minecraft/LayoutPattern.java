package me.saiintbrisson.minecraft;

import java.util.Stack;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.devnatan.inventoryframework.ViewItem;

@Getter
@ToString
@RequiredArgsConstructor
public class LayoutPattern {

    private final char character;
    private final Function<Integer, ViewItem> factory;
    private final Stack<Integer> slots = new Stack<>();
}
