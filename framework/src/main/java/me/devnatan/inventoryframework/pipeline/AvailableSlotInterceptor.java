package me.devnatan.inventoryframework.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.exception.SlotFillExceededException;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.VisibleForTesting;

public final class AvailableSlotInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext context = (IFRenderContext) subject;
        final List<ComponentFactory> slotComponentList = context.getConfig().getLayout() == null
                ? resolveFromInitialSlot(context)
                : resolveFromLayoutSlot(context);

        slotComponentList.forEach(componentFactory -> context.addComponent(componentFactory.create()));
    }

    /**
     * Resolves the components to register with their defined slots starting from the first
     * container slot.
     *
     * @param context The renderization context.
     */
    @VisibleForTesting
    List<ComponentFactory> resolveFromInitialSlot(IFRenderContext context) {
        final List<BiFunction<Integer, Integer, ComponentFactory>> availableSlots =
                context.getAvailableSlotsFactories();

        for (int i = 0; i < availableSlots.size(); i++) {}

        return Collections.emptyList();
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
        final List<Integer> fillablePositions = layoutSlot.getPositions();
        if (fillablePositions == null || fillablePositions.isEmpty()) return Collections.emptyList();

        final List<BiFunction<Integer, Integer, ComponentFactory>> availableSlots =
                context.getAvailableSlotsFactories();
        if (availableSlots.isEmpty()) return Collections.emptyList();

        final List<ComponentFactory> result = new ArrayList<>();
        for (int i = 0; i < availableSlots.size(); i++) {
            int slot;
            try {
                slot = fillablePositions.get(i);
            } catch (final ArrayIndexOutOfBoundsException exception) {
                throw new SlotFillExceededException("No more slots available", exception);
            }

            final ComponentFactory factory = availableSlots.get(i).apply(i, slot);
            result.add(factory);
        }

        return result;
    }
}
