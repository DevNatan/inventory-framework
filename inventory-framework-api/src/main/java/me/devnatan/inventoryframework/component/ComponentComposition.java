package me.devnatan.inventoryframework.component;

import java.util.List;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * A component whose is composed of multiple components.
 */
public interface ComponentComposition extends Component, Iterable<Component> {

    /**
     * All components in this composition.
     *
     * @return An unmodifiable view of all components in this composition.
     */
    @UnmodifiableView
    List<Component> getComponents();

    /**
     * Checks if any component of that composition is in a specific position.
     *
     * @param position The position to check.
     * @return If any component in this composition is contained in the given position.
     */
    @Override
    boolean isContainedWithin(int position);
}
