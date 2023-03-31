package me.devnatan.inventoryframework.internal;

import java.util.List;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import me.devnatan.inventoryframework.component.ComponentFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@Data
public final class LayoutSlot {

    public static final char FILLED_RESERVED_CHAR = 'O';

    private final char character;

    /**
     * The first parameter is the current iteration index.
     */
    @EqualsAndHashCode.Exclude
    @Nullable
    private final Function<Integer, ComponentFactory> factory;

    @EqualsAndHashCode.Exclude
    @Setter(AccessLevel.NONE)
    private List<Integer> positions;

    @ApiStatus.Internal
    public void updatePositions(List<Integer> positions) {
        if (this.positions != null) throw new IllegalStateException("Positions can only be updated once");

        this.positions = positions;
    }

    public boolean isDefinedByTheUser() {
        return factory != null;
    }
}
