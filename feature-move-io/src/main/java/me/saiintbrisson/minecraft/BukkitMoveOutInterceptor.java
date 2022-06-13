package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class BukkitMoveOutInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

	@Override
	public void intercept(
		@NotNull final PipelineContext<BukkitClickViewSlotContext> pipeline,
		final BukkitClickViewSlotContext subject
	) {
		if (subject.isCancelled())
			return;

		final InventoryClickEvent event = subject.getClickOrigin();
		final InventoryAction action = event.getAction();

		// fast path -- from entity container to view container is detected on move in interceptor
		if (!subject.isOnEntityContainer())
			return;

		// cannot move items to the view container with shift click
		if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY
			&& subject.getRoot().isCancelOnMoveIn()
			&& event.getClick().isShiftClick()
		) {
			subject.setCancelled(true);
			event.setCancelled(true);
			pipeline.finish();
			return;
		}

		if (action != InventoryAction.PLACE_ALL &&
			action != InventoryAction.PLACE_ONE &&
			action != InventoryAction.PLACE_SOME &&
			action != InventoryAction.SWAP_WITH_CURSOR
		) return;

		final ItemStack swappedItem = action == InventoryAction.SWAP_WITH_CURSOR
			? event.getCurrentItem()
			: null;

		ViewItem hold = null;

		final ViewContainer container = subject.getContainer();

		// fast path -- items defined on root are all static so we skip them
		final boolean resolveOnRoot = false;
		final boolean entityContainer = subject.isOnEntityContainer();

		for (int i = container.getFirstSlot(); i <= container.getLastSlot(); i++) {
			final ViewItem item = subject.resolve(i, resolveOnRoot, entityContainer);
			if (item == null) continue;

			// fast path -- skip not yet hold items
			if (item.getState() != ViewItem.State.HOLDING) continue;

			hold = item;
			break;
		}

		if (hold == null)
			return;

		final ViewSlotMoveContext moveContext = new BukkitViewSlotMoveContextImpl(
			hold,
			subject,
			event,
			container,
			event.getCursor(),
			swappedItem,
			event.getSlot(),
			swappedItem != null,
			false
		);

		moveContext.setCancelled(subject.isCancelled());
		subject.getRoot().onMoveOut(moveContext);
		event.setCancelled(moveContext.isCancelled());

		if (!moveContext.isCancelled())
			return;

		pipeline.finish();
	}

}
