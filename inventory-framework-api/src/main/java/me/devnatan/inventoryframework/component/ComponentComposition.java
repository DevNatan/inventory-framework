package me.devnatan.inventoryframework.component;

/**
 * A component whose is composed of multiple components.
 */
public interface ComponentComposition extends Component, ComponentContainer, Iterable<Component> {}
