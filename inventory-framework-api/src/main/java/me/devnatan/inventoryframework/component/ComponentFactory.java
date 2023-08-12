package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.NotNull;

/**
 * Factory for {@link Component} interface.
 */
public interface ComponentFactory {

    /**
     * Creates a new component.
     *
     * @return A new component instance.
     */
    @NotNull
    Component create();
}
