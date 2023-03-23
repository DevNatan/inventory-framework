package me.devnatan.inventoryframework.pipeline;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.exception.InvalidLayoutException;
import me.devnatan.inventoryframework.internal.LayoutSlot;

public final class LayoutInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext renderContext = (IFRenderContext) subject;
        final String[] layout = renderContext.getConfig().getLayout();
        if (layout == null || layout.length == 0) return;

        final Map<Character, List<Integer>> slots = resolveLayout(renderContext, layout);
        registerLayoutComponents(renderContext, slots);
    }

    private void registerLayoutComponents(IFRenderContext context, Map<Character, List<Integer>> slots) {
        for (final Map.Entry<Character, List<Integer>> entry : slots.entrySet()) {
            final Optional<LayoutSlot> layoutSlotOption = context.getLayoutSlots().stream()
                    .filter(layoutSlot -> layoutSlot.getCharacter() == entry.getKey())
                    .findFirst();

            if (!layoutSlotOption.isPresent()) continue;

            final LayoutSlot layoutSlot = layoutSlotOption.get();
            if (layoutSlot.getCharacter() != LayoutSlot.FILLED_RESERVED_CHAR) {
                final Function<Integer, ComponentFactory> factory = layoutSlot.getFactory();
                int iterationIndex = 0;

                for (final int slot : entry.getValue()) {
                    final ComponentFactory componentFactory = factory.apply(iterationIndex++);
                    if (componentFactory instanceof ItemComponentBuilder)
                        ((ItemComponentBuilder<?>) componentFactory).withSlot(slot);

                    final Component component = componentFactory.create();
                    context.addComponent(component);
                }
            }

            layoutSlot.updatePositions(entry.getValue());
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

                slots.computeIfAbsent(character, ArrayList::new).add(slotIdx);
            }
        }

        return slots;
    }
}
