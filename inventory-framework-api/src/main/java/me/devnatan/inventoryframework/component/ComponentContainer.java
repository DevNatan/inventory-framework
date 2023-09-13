package me.devnatan.inventoryframework.component;

import java.util.List;
import org.jetbrains.annotations.UnmodifiableView;

public interface ComponentContainer extends Iterable<Component> {

    /**
     * All components in this container.
     *
     * @return An unmodifiable view of all components in this container.
     */
    @UnmodifiableView
    List<Component> getComponents();
}
