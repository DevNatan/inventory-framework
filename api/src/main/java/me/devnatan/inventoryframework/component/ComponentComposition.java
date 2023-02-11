package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface ComponentComposition extends Component, Iterable<Component> {

	/**
	 * All components in this composition.
	 *
	 * @return An unmodifiable List view of all components in this composition.
	 */
	@UnmodifiableView
	List<Component> getComponents();

	/**
	 * Checks if any component of that composition is in a specific position.
	 *
	 * @param position The position.
	 * @return If any component in this composition is contained in the given position.
	 */
	boolean isContainedWithin(int position);

	/**
	 * The minimum position of this composition of components.
	 *
	 * @return The position of the component where it is closest to the start of the container.
	 */
	int getMinimumPosition();

	/**
	 * The maximum position of this composition of components.
	 *
	 * @return The position of the component where it is closest to the end of the container.
	 */
	int getMaximumPosition();
}
