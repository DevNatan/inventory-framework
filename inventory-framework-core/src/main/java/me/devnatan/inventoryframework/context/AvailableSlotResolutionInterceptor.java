package me.devnatan.inventoryframework.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.exception.SlotFillExceededException;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.VisibleForTesting;

final class AvailableSlotResolutionInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext context = (IFRenderContext) subject;
        if (context.getAvailableSlotFactories() == null) return;

        final List<ComponentBuilder> slotComponents = context.getConfig().getLayout() == null
                ? resolveFromInitialSlot(context)
                : resolveFromLayoutSlot(context);

        slotComponents.forEach(componentFactory -> context.addComponent(componentFactory.buildComponent(context)));
    }

    /**
     * Resolves the components to register with their defined slots starting from the first
     * container slot.
     *
     * @param context The rendering context.
     */
    @VisibleForTesting
    List<ComponentBuilder> resolveFromInitialSlot(IFRenderContext context) {
        final List<BiFunction<Integer, Integer, ComponentBuilder>> availableSlotFactories =
                context.getAvailableSlotFactories();
        final List<ComponentBuilder> result = new ArrayList<>();

        int slot = 0;
        for (int i = 0; i < context.getContainer().getSize(); i++) {
            while (isSlotNotAvailableForAutoFilling(context, slot)) slot++;

            try {
                final BiFunction<Integer, Integer, ComponentBuilder> factory = availableSlotFactories.get(i);
                result.add(factory.apply(i, slot++));
            } catch (IndexOutOfBoundsException ignored) {
                break;
            }
        }

        return result;
    }

    /**
     * Resolves the components to be registered with their defined slots respecting the limits of
     * the current layout.
     *
     * @param context The rendering context.
     */
    @VisibleForTesting
    List<ComponentBuilder> resolveFromLayoutSlot(IFRenderContext context) {
        final Optional<LayoutSlot> layoutSlotOption = context.getLayoutSlots().stream()
                .filter(layoutSlot -> layoutSlot.getCharacter() == LayoutSlot.DEFAULT_SLOT_FILL_CHAR)
                .findFirst();

        if (!layoutSlotOption.isPresent()) return Collections.emptyList();

        final LayoutSlot layoutSlot = layoutSlotOption.get();
        final int[] fillablePositions = layoutSlot.getPositions();

        // Positions may be null if the layout has not yet been resolved
        if (fillablePositions == null || fillablePositions.length == 0) return Collections.emptyList();

        final List<BiFunction<Integer, Integer, ComponentBuilder>> availableSlotFactories =
                context.getAvailableSlotFactories();

        final List<ComponentBuilder> result = new ArrayList<>();
        // Offset is incremented for each unavailable slot found
        int offset = 0;

        for (int i = 0; i < availableSlotFactories.size(); i++) {
            int slot;
            try {
                slot = fillablePositions[i + offset];
            } catch (final IndexOutOfBoundsException exception) {
                throw new SlotFillExceededException("Capacity to accommodate items in the layout"
                        + " for items in available slots has been exceeded.");
            }

            // if the selected slot is not available for autofill, move it until
            // we find the next an available position
            while (isSlotNotAvailableForAutoFilling(context, slot)) {
                try {
                    slot = fillablePositions[i + (++offset)];
                } catch (final IndexOutOfBoundsException exception) {
                    throw new SlotFillExceededException(String.format(
                            "Capacity to accommodate items in the layout for items"
                                    + " in available slots has been exceeded. "
                                    + "Tried to set an item from index %d from position %d to another, "
                                    + "but it breaks the layout rules",
                            i, slot));
                }
            }

            final BiFunction<Integer, Integer, ComponentBuilder> factory = availableSlotFactories.get(i);
            result.add(factory.apply(i, slot));
        }

        return result;
    }

    private boolean isSlotNotAvailableForAutoFilling(IFRenderContext context, int slot) {
        if (!context.getContainer().getType().canPlayerInteractOn(slot)) return true;

        // fast path -- check for already rendered items
        if (context.getContainer().hasItem(slot)) return true;

        // we need to check component factories since components don't have been yet rendered
        // TODO Find a better way to check this "noneMatch" to remove isContainedWithin from builder
        //      since slot can be re-defined on render and this interceptor runs before it
        return context.getNotRenderedComponents().stream()
                .filter(componentFactory -> componentFactory instanceof ItemComponentBuilder)
                .map(componentFactory -> (ItemComponentBuilder) componentFactory)
                .anyMatch(itemBuilder -> itemBuilder.isContainedWithin(slot));
    }
}
