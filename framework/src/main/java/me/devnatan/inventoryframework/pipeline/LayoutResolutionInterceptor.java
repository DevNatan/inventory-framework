package me.devnatan.inventoryframework.pipeline;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.exception.InvalidLayoutException;
import org.jetbrains.annotations.NotNull;

public final class LayoutResolutionInterceptor implements PipelineInterceptor<IFContext> {

    public static final char LAYOUT_FILLED = 'O';

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext context) {
        if (!(context instanceof IFRenderContext))
            throw new IllegalArgumentException("LayoutResolutionInterceptor subject must be a render context");

        final String[] layout = context.getConfig().getLayout();
        if (layout == null) return;
        if (layout.length == 0) return;

        resolveLayout((IFRenderContext) context, layout);
    }

    private void resolveLayout(IFRenderContext context, @NotNull String[] layout) {
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

                context.getLayoutSlots().stream()
                        .filter(pattern -> pattern.getCharacter() == character)
                        .findFirst()
                        .ifPresent(layoutSlot -> {
                            layoutSlot.getSlots().push(slotIdx);
                            System.out.println("LayoutResolutionInterceptor: applied " + slotIdx + " to " + layoutSlot);
                        });
            }
        }
    }
}
