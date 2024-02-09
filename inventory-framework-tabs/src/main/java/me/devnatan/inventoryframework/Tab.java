package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.SlotComponentRenderer;
import me.devnatan.inventoryframework.pipeline.Pipelined;
import me.devnatan.inventoryframework.state.StateValueHost;

public interface Tab<C, B> extends StateValueHost, Pipelined, SlotComponentRenderer<C, B> {
}
