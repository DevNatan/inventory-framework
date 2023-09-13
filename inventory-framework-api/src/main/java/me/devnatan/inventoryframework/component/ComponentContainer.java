package me.devnatan.inventoryframework.component;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

public interface ComponentContainer extends Iterable<Component> {

    /**
     * All components in this container.
     *
     * @return An unmodifiable view of all components in this container.
     */
    @UnmodifiableView
    List<Component> getComponents();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    List<Component> getInternalComponents();
}
