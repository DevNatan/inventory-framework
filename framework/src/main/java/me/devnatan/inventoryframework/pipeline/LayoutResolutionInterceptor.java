package me.devnatan.inventoryframework.pipeline;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.exception.InvalidLayoutException;
import org.jetbrains.annotations.NotNull;

public final class LayoutResolutionInterceptor implements PipelineInterceptor<IFContext> {

    private static final char LAYOUT_FILLED = 'O';

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
        final String[] layout = context.getConfig().getLayout();
        if (layout == null) return;
        if (layout.length == 0) return;

        resolveLayout(context, layout);
    }

    private void resolveLayout(IFContext context, String[] layout) {
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

                if (character == LAYOUT_FILLED) {
                    slots.computeIfAbsent(character, ArrayList::new).add(slotIdx);
                    continue;
                }
            }
        }
    }
}
