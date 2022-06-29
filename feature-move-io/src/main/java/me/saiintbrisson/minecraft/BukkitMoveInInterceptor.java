package me.saiintbrisson.minecraft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
final class SlotFindResult {

	private final int value;
	private final int moveTo;
	private final boolean stacked;

	public boolean isAvailable() {
		return value != -1;
	}

	public boolean shouldBeMoved() {
		return moveTo != -1;
	}

}

public final class BukkitMoveInInterceptor
	implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(
		@NotNull final PipelineContext<BukkitClickViewSlotContext> pipeline,
		final BukkitClickViewSlotContext subject) {
		if (subject.isCancelled()) return;

		final InventoryClickEvent event = subject.getClickOrigin();
		final InventoryAction action = event.getAction();

		// fast path -- no need to handle hot bar swap
		if (action == InventoryAction.HOTBAR_SWAP)
			return;

		if (subject.isOnEntityContainer()) {
			// shift-clicked the item and moved it to the view's inventory
			if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				final ItemStack item = Objects.requireNonNull(event.getCurrentItem(),
					"Shift-moved item cannot be null on move to other inventory action"
				);

				final SlotFindResult availableSlot = findNextAvailableSlot(subject, item);

				// there is no slot available for the item to be moved, the event must be canceled,
				// and we return the method as successful it is not possible to proceed but the move
				// in was handled.
				if (!availableSlot.isAvailable()) {
					subject.setCancelled(true);
					return;
				}

				// TODO check for swap and stack
				final ViewSlotMoveContext moveInContext = new ViewSlotMoveContextImpl(
					subject.getBackingItem(),
					subject,
					event,
					subject.getContainer(),
					item,
					null,
					availableSlot.getValue(),
					false,
					false
				);
				subject.getRoot().runCatching(moveInContext, () ->
					subject.getRoot().onMoveIn(moveInContext));

				subject.setCancelled(subject.isCancelled() || moveInContext.isCancelled());

				if (subject.isCancelled())
					return;

				// in some cases the item must be moved, like PaginatedView with a defined layout
				// the slot that the item will be moved will be the slot that respects the conditions of that view
				if (availableSlot.shouldBeMoved()) {
					// TODO item should be moved to "move to"
				}
			}
		}
	}

	private SlotFindResult findNextAvailableSlot(
		@NotNull ViewContext context,
		@NotNull ItemStack currentItem
	) {
		int moveTo = -1;
		boolean stacked = false;

		int idx = 0;
		do {
			// first we try to get a static item from the view
			final ViewItem item = context.getItem(idx);
			if (item != null) {
				final ItemStack staticItem = (ItemStack) context.getRoot().unwrap(item.getItem());

				// we can determine if slot is available only with fallback item items rendered
				// through the rendering function cannot be accessed
				if (staticItem != null) {
					// TODO stack detection
					if (staticItem.isSimilar(currentItem)) {
						stacked = true;
						break;
					}
				}

				continue;
			}

			// checks if there is an item in that slot that was "manually" to the inventory, like
			// another move in
			final ItemStack actualItem = (ItemStack) context.getRoot().unwrap(
				context.getContainer().getItem(idx)
			);
			if (actualItem != null) {
				if (actualItem.isSimilar(currentItem)) {
					// TODO stack detection
					stacked = true;
					break;
				}

				continue;
			}
		} while (idx++ <= context.getLastSlot());

		return new SlotFindResult(idx, moveTo, stacked);
	}

}
