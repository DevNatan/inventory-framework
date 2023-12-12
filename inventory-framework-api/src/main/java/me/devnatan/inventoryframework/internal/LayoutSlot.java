package me.devnatan.inventoryframework.internal;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import org.jetbrains.annotations.Nullable;

public final class LayoutSlot {

    public static final char DEFAULT_SLOT_FILL_CHAR = 'O';

    private final char character;
    private final IntFunction<ComponentBuilder> builderFactory;
    private final IntFunction<Component> componentFactory;
    private final int[] positions;

    public LayoutSlot(
            char character,
            IntFunction<ComponentBuilder> builderFactory,
            IntFunction<Component> componentFactory,
            int[] positions) {
        this.character = character;
        this.builderFactory = builderFactory;
        this.componentFactory = componentFactory;
        this.positions = positions;
    }

    public char getCharacter() {
        return character;
    }

    public IntFunction<ComponentBuilder> getBuilderFactory() {
        return builderFactory;
    }

    public IntFunction<Component> getComponentFactory() {
        return componentFactory;
    }

    public LayoutSlot withBuilderFactory(@Nullable IntFunction<ComponentBuilder> factory) {
        return new LayoutSlot(character, factory, componentFactory, positions);
    }

    public LayoutSlot withComponentFactory(@Nullable IntFunction<Component> factory) {
        return new LayoutSlot(character, builderFactory, factory, positions);
    }

    public int[] getPositions() {
        return positions;
    }

    public boolean isInPosition(int position) {
        for (final int layoutPosition : getPositions()) {
            if (layoutPosition == position) return true;
        }
        return false;
    }

    public boolean isDefinedByTheUser() {
        return builderFactory != null;
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
        return "LayoutSlot{" + "character=" + character + ", builderFactory=" + builderFactory + ", componentFactory="
                + componentFactory + ", positions=" + Arrays.toString(positions) + '}';
    }
}
