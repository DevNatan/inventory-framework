package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFContext;

/**
 * Mark interface that throws an exception when an inventory modification is triggered.
 */
public interface UnmodifiableViewContext extends IFContext {

    @Override
    default void inventoryModificationTriggered() {
        throw new IllegalStateException("Not allowed to modify the inventory in this context.");
    }
}
