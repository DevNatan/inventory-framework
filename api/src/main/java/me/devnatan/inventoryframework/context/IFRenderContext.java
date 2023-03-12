package me.devnatan.inventoryframework.context;

import java.util.List;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface IFRenderContext extends IFConfinedContext {

    IFContext getParent();

    @NotNull
    @UnmodifiableView
    List<ComponentBuilder<?>> getRegisteredComponentBuilders();

    @NotNull
    @UnmodifiableView
    List<LayoutSlot> getLayoutSlots();
}
