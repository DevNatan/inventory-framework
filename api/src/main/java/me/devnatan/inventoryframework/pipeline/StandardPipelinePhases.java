package me.devnatan.inventoryframework.pipeline;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardPipelinePhases {

    public static final PipelinePhase OPEN = new PipelinePhase("open"),
            INIT = new PipelinePhase("init"),
            RENDER = new PipelinePhase("render"),
            SLOT_RENDER = new PipelinePhase("slot-render"),
            PAGINATED_ITEM_RENDER = new PipelinePhase("paginated-item-render"),
            RESUME = new PipelinePhase("resume"),
            UPDATE = new PipelinePhase("update"),
            CLICK = new PipelinePhase("click"),
            CLOSE = new PipelinePhase("close");
}
