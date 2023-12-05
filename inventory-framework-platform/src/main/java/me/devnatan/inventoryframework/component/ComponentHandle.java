package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.state.StateAccess;
import org.jetbrains.annotations.NotNull;

public interface ComponentHandle<CONTEXT, COMPONENT_BUILDER> extends StateAccess<CONTEXT, COMPONENT_BUILDER> {
}
