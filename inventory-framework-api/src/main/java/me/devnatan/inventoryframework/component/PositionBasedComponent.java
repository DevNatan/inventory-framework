package me.devnatan.inventoryframework.component;

/**
 * An interface for components that are positioned within a view.
 */
public interface PositionBasedComponent extends Component {

    /**
     * Retrieves the position of the component within its view.
     *
     * @return The position of the component.
     */
    int getPosition();

    /**
     * Sets the position of the component within its view.
     *
     * @param position The new position for the component.
     */
    void setPosition(int position);
}
