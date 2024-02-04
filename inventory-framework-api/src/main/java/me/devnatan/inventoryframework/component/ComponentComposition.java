package me.devnatan.inventoryframework.component;

import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

/**
 * A component whose is composed of multiple components.
 */
public interface ComponentComposition extends Component, ComponentContainer, Iterable<Component> {

    @NotNull
    @Override
    default Iterator<Component> iterator() {
        return getComponents().iterator();
    }
}
