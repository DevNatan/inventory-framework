package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface IFRenderContext extends IFConfinedContext {
    @NotNull
    @UnmodifiableView
    @ApiStatus.Internal
    List<ComponentFactory> getComponentFactories();

    @ApiStatus.Internal
    List<LayoutSlot> getLayoutSlots();

    @ApiStatus.Internal
    void addLayoutSlot(@NotNull LayoutSlot layoutSlot);

    @ApiStatus.Internal
    BiFunction<Integer, Integer, ComponentFactory> getAvailableSlotFactory();
}
