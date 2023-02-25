package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.component.IFItem;

@FunctionalInterface
public interface ItemHandler {

    void handle(IFItem item);
}
