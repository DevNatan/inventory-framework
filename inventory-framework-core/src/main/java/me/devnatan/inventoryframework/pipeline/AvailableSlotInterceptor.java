package me.devnatan.inventoryframework.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.exception.SlotFillExceededException;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.VisibleForTesting;

public final class AvailableSlotInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext context = (IFRenderContext) subject;
        if (context.getAvailableSlotFactory() == null) return;

        final List<ComponentFactory> slotComponents = context.getConfig().getLayout() == null
                ? resolveFromInitialSlot(context)
                : resolveFromLayoutSlot(context);

        slotComponents.forEach(componentFactory -> context.addComponent(componentFactory.create()));
    }

    /**
     * Resolves the components to register with their defined slots starting from the first
     * container slot.
     *
     * @param context The renderization context.
     */
    @VisibleForTesting
    List<ComponentFactory> resolveFromInitialSlot(IFRenderContext context) {
        final BiFunction<Integer, Integer, ComponentFactory> availableSlotFactory = context.getAvailableSlotFactory();
        final List<ComponentFactory> result = new ArrayList<>();

        int slot = 0;
        for (int i = 0; i < context.getContainer().getSize(); i++) {
            while (!isSlotAvailableForAutoFilling(context, slot)) slot++;

            result.add(availableSlotFactory.apply(i, slot++));
        }

        return result;
    }

    /**
     * Resolves the components to be registered with their defined slots respecting the limits of
     * the current layout.
     *
     * @param context The renderization context.
     */
    @VisibleForTesting
    List<ComponentFactory> resolveFromLayoutSlot(IFRenderContext context) {
        final Optional<LayoutSlot> layoutSlotOption = context.getLayoutSlots().stream()
                .filter(layoutSlot -> layoutSlot.getCharacter() == LayoutSlot.FILLED_RESERVED_CHAR)
                .findFirst();

        if (!layoutSlotOption.isPresent()) return Collections.emptyList();

        final LayoutSlot layoutSlot = layoutSlotOption.get();
        final int[] fillablePositions = layoutSlot.getPositions();

        // positions may be null if the layout has not yet been resolved
        if (fillablePositions == null || fillablePositions.length == 0) return Collections.emptyList();

        final BiFunction<Integer, Integer, ComponentFactory> availableSlotFactory = context.getAvailableSlotFactory();

        final List<ComponentFactory> result = new ArrayList<>();
        int offset = 0; // incremented for each unavailable slot found

        for (int i = 0; i < fillablePositions.length; i++) {
            int slot;
            try {
                slot = fillablePositions[i + offset];
            } catch (final IndexOutOfBoundsException exception) {
                throw new SlotFillExceededException("Capacity to accommodate items in the layout"
                        + " for items in available slots has been exceeded.");
            }

            // if the selected slot is not available for autofill, move it until
            // we find the next an available position
            while (!isSlotAvailableForAutoFilling(context, slot)) {
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

            result.add(availableSlotFactory.apply(i, slot));
        }

        return result;
    }

    private boolean isSlotAvailableForAutoFilling(IFRenderContext context, int slot) {
        if (!context.getContainer().getType().canPlayerInteractOn(slot)) return false;

        // fast path -- check for already rendered items
        if (context.getContainer().hasItem(slot)) return false;

        // we need to check component factories since components don't have been yet rendered
        return context.getComponentFactories().stream()
                .filter(componentFactory -> componentFactory instanceof ItemComponentBuilder)
                .map(componentFactory -> (ItemComponentBuilder<?, ?>) componentFactory)
                .noneMatch(itemBuilder -> itemBuilder.isContainedWithin(slot));
    }
}
