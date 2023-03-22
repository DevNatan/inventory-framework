package me.devnatan.inventoryframework.internal;

import java.util.function.Function;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.devnatan.inventoryframework.component.ComponentBuilder;

@Data
public final class LayoutSlot {

    public static final char FILLED_RESERVED_CHAR = 'O';

    private final char character;

    /**
     * The first parameter is the current iteration index.
     */
    @EqualsAndHashCode.Exclude
    private final Function<Integer, ComponentBuilder<?>> factory;
}
