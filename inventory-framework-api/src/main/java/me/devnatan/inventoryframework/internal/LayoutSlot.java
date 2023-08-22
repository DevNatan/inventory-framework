package me.devnatan.inventoryframework.internal;

import java.util.Arrays;
import java.util.Objects;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.jdk.IndexSlotFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class LayoutSlot {

    // Retro compatibility
    public static final char FILLED_RESERVED_CHAR = 'O';

    private final char character;
    private final IndexSlotFunction<ComponentFactory> factory;
    private final int[] positions;

    public LayoutSlot(char character, @Nullable IndexSlotFunction<ComponentFactory> factory, int[] positions) {
        this.character = character;
        this.factory = factory;
        this.positions = positions;
    }

    public char getCharacter() {
        return character;
    }

    public IndexSlotFunction<ComponentFactory> getFactory() {
        return factory;
    }

    public LayoutSlot withFactory(@Nullable IndexSlotFunction<ComponentFactory> factory) {
        return new LayoutSlot(character, factory, positions);
    }

    public int[] getPositions() {
        return positions;
    }

    public boolean isDefinedByTheUser() {
        return factory != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LayoutSlot that = (LayoutSlot) o;
        return getCharacter() == that.getCharacter();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCharacter());
    }

    @Override
    public String toString() {
        return "LayoutSlot{" + "character=" + character + ", factory=" + factory + ", positions="
                + Arrays.toString(positions) + '}';
    }
}
