package me.devnatan.inventoryframework.state;

import lombok.RequiredArgsConstructor;
import me.saiintbrisson.minecraft.exception.InventoryModificationException;

import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
class InternalStateHolder {

	private static final AtomicLong ids = new AtomicLong();

	private final Object initialValue;
	private final boolean immutable;
	private final long id = ids.getAndIncrement();

	private Object currValue = initialValue;

	public void updateCaught(StateOwner target, Object newValue) {
		if (immutable)
			throw new IllegalStateException("Cannot update immutable state: " + id);

		// TODO atomic update
		this.currValue = newValue;
	}

}
