package me.devnatan.inventoryframework.context;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.devnatan.inventoryframework.exception.InvalidLayoutException;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;

final class LayoutResolutionInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext renderContext = (IFRenderContext) subject;
        final String[] layout = renderContext.getConfig().getLayout();
        if (layout == null || layout.length == 0) return;

        final Map<Character, List<Integer>> slots = resolveLayout(renderContext, layout);
        for (final Map.Entry<Character, List<Integer>> entry : slots.entrySet()) {
            final LayoutSlot layoutSlot = new LayoutSlot(
                    entry.getKey(),
                    null,
                    null,
                    entry.getValue().stream().mapToInt($ -> $).toArray());
            renderContext.addLayoutSlot(layoutSlot);
        }
    }

    private Map<Character, List<Integer>> resolveLayout(IFRenderContext context, String[] layout) {
        final int layoutRows = layout.length;
        final int containerRows = context.getContainer().getRowsCount();

        if (layoutRows != containerRows)
            throw new InvalidLayoutException(format(
                    "Layout length (%d) must respect the rows count of the container (%d).",
                    layoutRows, containerRows));

        final int containerColumns = context.getContainer().getColumnsCount();
        final Map<Character, List<Integer>> slots = new HashMap<>();

        for (int row = 0; row < layoutRows; row++) {
            final String layer = layout[row];
            final int layerLen = layer.length();
            if (layerLen != containerColumns)
                throw new InvalidLayoutException(format(
                        "Layout layer length located at %d must respect the columns count of the"
                                + " container (given: %d, expect: %d).",
                        row, layerLen, containerColumns));

            for (int col = 0; col < containerColumns; col++) {
                final int slotIdx = col + (row * containerColumns);
                final char character = layer.charAt(col);
                if (character == ' ') continue;

                slots.computeIfAbsent(character, ArrayList::new).add(slotIdx);
            }
        }

        return slots;
    }
}
