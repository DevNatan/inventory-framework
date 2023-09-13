package me.devnatan.inventoryframework.component;

/**
 * A component whose is composed of multiple components.
 */
public interface ComponentComposition extends Component, ComponentContainer {

    /**
     * Checks if any component of that composition is in a specific position.
     *
     * @param position The position to check.
     * @return If any component in this composition is contained in the given position.
     */
    @Override
    boolean isContainedWithin(int position);
}
