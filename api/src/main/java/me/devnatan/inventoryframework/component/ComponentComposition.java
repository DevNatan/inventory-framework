package me.devnatan.inventoryframework.component;

import java.util.List;
import org.jetbrains.annotations.UnmodifiableView;

public interface ComponentComposition extends Component, Iterable<Component> {

    /**
     * All components in this composition.
     *
     * @return An unmodifiable List view of all components in this composition.
     */
    @UnmodifiableView
    List<Component> getComponents();

    /**
     * Checks if <b>any component of that composition</b> is in a specific position.
     *
     * @param position The position.
     * @return If any component in this composition is contained in the given position.
     */
    @Override
    boolean isContainedWithin(int position);
}
