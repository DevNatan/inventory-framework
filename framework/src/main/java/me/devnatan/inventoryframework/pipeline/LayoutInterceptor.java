package me.devnatan.inventoryframework.pipeline;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.exception.InvalidLayoutException;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.NotNull;

public final class LayoutInterceptor implements PipelineInterceptor<IFContext> {

    public static final char LAYOUT_FILLED = 'O';

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext context) {
        if (!(context instanceof IFRenderContext)) return;

        final String[] layout = context.getConfig().getLayout();
        if (layout == null) return;
        if (layout.length == 0) return;

        final IFRenderContext renderContext = (IFRenderContext) context;
        final Map<Character, List<Integer>> slots = resolveLayout(renderContext, layout);
        registerLayout(renderContext, slots);
    }

    private void registerLayout(IFRenderContext context, Map<Character, List<Integer>> slots) {
        final ElementFactory elementFactory = context.getRoot().getElementFactory();
        for (final Map.Entry<Character, List<Integer>> entry : slots.entrySet()) {
            final Optional<LayoutSlot> layoutSlotOptional = context.getLayoutSlots().stream()
                    .filter(layoutSlot -> layoutSlot.getCharacter() == entry.getKey())
                    .findFirst();
            if (!layoutSlotOptional.isPresent()) continue;

            final Function<Integer, ComponentBuilder<?>> factory =
                    layoutSlotOptional.get().getFactory();
            int iterationIndex = 0;
            for (final int slot : entry.getValue()) {
                final ComponentBuilder<?> builder = factory.apply(iterationIndex++);
                if (builder instanceof ItemComponentBuilder) ((ItemComponentBuilder<?>) builder).withSlot(slot);

                final Component component = elementFactory.buildComponent(builder);
                context.addComponent(component);
            }
        }
    }

    private Map<Character, List<Integer>> resolveLayout(IFRenderContext context, @NotNull String[] layout) {
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
