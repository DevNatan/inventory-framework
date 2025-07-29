package me.devnatan.inventoryframework.internal;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;
import me.devnatan.inventoryframework.component.ComponentFactory;
import org.jetbrains.annotations.Nullable;

public final class LayoutSlot {

    // Retro compatibility
    public static final char FILLED_RESERVED_CHAR = 'O';

    private final char character;
    private final IntFunction<ComponentFactory> factory;
    private final int[] positions;
	private final boolean fill;

    public LayoutSlot(char character, @Nullable IntFunction<ComponentFactory> factory, int[] positions, boolean fill) {
        this.character = character;
        this.factory = factory;
        this.positions = positions;
		this.fill = fill;
    }

    public char getCharacter() {
        return character;
    }

    public IntFunction<ComponentFactory> getFactory() {
        return factory;
    }

    public LayoutSlot withFactory(@Nullable IntFunction<ComponentFactory> factory) {
        return new LayoutSlot(character, factory, positions, fill);
    }

	public LayoutSlot withFill(boolean fill) {
		return new LayoutSlot(character, factory, positions, fill);
	}

    public int[] getPositions() {
        return positions;
    }

    public boolean isDefinedByTheUser() {
        return factory != null;
    }

	public boolean isToFill() {
		return fill;
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
                + Arrays.toString(positions) + ", fill=" + fill + "}";
    }
}
