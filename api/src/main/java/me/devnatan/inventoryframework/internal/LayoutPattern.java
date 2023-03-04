package me.devnatan.inventoryframework.internal;

import java.util.Stack;
import java.util.function.Function;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;

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
    private final Function<Integer, ItemComponentBuilder<?>> factory;
}
