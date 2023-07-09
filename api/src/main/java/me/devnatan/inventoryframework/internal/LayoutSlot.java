package me.devnatan.inventoryframework.internal;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import me.devnatan.inventoryframework.component.ComponentFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class LayoutSlot {

    // Retro compatibility
    public static final char FILLED_RESERVED_CHAR = 'O';

    private final char character;
    private final Function<Integer, ComponentFactory> factory;
    private List<Integer> positions;

    public LayoutSlot(char character, @Nullable Function<Integer, ComponentFactory> factory) {
        this.character = character;
        this.factory = factory;
    }

    public char getCharacter() {
        return character;
    }

    // The first parameter is the current iteration index.
    public Function<Integer, ComponentFactory> getFactory() {
        return factory;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public boolean isDefinedByTheUser() {
        return factory != null;
    }

    @ApiStatus.Internal
    public void updatePositions(List<Integer> positions) {
        if (this.positions != null) throw new IllegalStateException("Positions can only be updated once");

        this.positions = positions;
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
        return "LayoutSlot{" + "character=" + character + ", factory=" + factory + ", positions=" + positions + '}';
    }
}
