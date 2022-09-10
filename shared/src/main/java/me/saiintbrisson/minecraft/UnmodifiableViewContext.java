package me.saiintbrisson.minecraft;

/**
 * Mark interface that throws an exception when an inventory modification is triggered.
 */
public interface UnmodifiableViewContext extends ViewContext {

    @Override
    default void inventoryModificationTriggered() {
        throw new IllegalStateException("Not allowed to modify the inventory in this context.");
    }
}
