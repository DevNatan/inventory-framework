package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.NotNull;

public interface ComponentFactory {

    @NotNull
    Component create();
}
