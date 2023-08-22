package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface IFRenderContext extends IFConfinedContext {

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @UnmodifiableView
    @ApiStatus.Internal
    List<ComponentFactory> getComponentFactories();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    List<LayoutSlot> getLayoutSlots();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void addLayoutSlot(@NotNull LayoutSlot layoutSlot);

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    BiFunction<Integer, Integer, ComponentFactory> getAvailableSlotFactory();

    /**
     * Adds a new component to this context, without restrictions.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param component The component.
     */
    @ApiStatus.Experimental
    void addComponent(@NotNull Component component);
}
