package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import static java.lang.String.format;

@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractViewSlotContext extends BaseViewContext implements ViewSlotContext {

	@Getter(AccessLevel.NONE)
	private final BaseViewContext parent;

	@Getter(AccessLevel.PACKAGE)
	private final ViewItem backingItem;

	@ToString.Include
	private boolean cancelled;

	@Setter(AccessLevel.NONE)
	private ItemWrapper item;

	private boolean changed;

	AbstractViewSlotContext(ViewItem backingItem, @NotNull final BaseViewContext parent) {
		super(parent.getRoot(), parent.getContainer());
		this.backingItem = backingItem;
		this.parent = parent;
	}

	@Override
	public final @NotNull ViewContextAttributes getAttributes() {
		return parent.getAttributes();
	}

	@Override
	protected final ViewItem[] getItems() {
		return parent.getItems();
	}

	@Override
	public final @NotNull ItemWrapper getItem() {
		return item;
	}

	@Override
	public final void updateItem(Consumer<ItemWrapper> updater) {
		inventoryModificationTriggered();
		updater.accept(this.item);
		setChanged(true);
	}

	@Override
	public final void setItem(@Nullable final Object item) {
		inventoryModificationTriggered();
		this.item = new ItemWrapper(PlatformUtils.getFactory().createItem(item));
		setChanged(true);
	}

	@Override
	public ViewSlotContext withItem(@Nullable Object item) {
		setItem(item);
		return this;
	}

	@Override
	public final boolean hasChanged() {
		return changed;
	}

	public final void setChanged(final boolean changed) {
		this.changed = changed;
	}

	@Override
	public final void setPropagateErrors(boolean propagateErrors) {
		throw new IllegalStateException(
			"Error propagation attribute cannot be modified from a ViewSlotContext." +
			"You must set this in view's #onRender(...) or globally on constructor instead."
		);
	}

	@Override
	public final void updateSlot() {
		final Runnable job = () -> getRoot().update(this, getBackingItem(), getSlot());
		final PlatformViewFrame<?, ?, ?> vf = getRoot().getViewFrame();
		if (vf == null) {
			job.run();
			return;
		}

		getRoot().getViewFrame().nextTick(job);
	}

	@Override
	public final <T> T data(@NotNull String key) {
		final Map<String, Object> data = backingItem.getData();
		if (data != null && data.containsKey(key))
			//noinspection unchecked
			return (T) data.get(key);

		throw new NoSuchElementException(format(
			"Property \"%s\" has not been set for this item. Use #withData(key, value) to set it",
			key
		));
	}

	@Override
	public int getSlot() {
		return getBackingItem().getSlot();
	}

	@Override
	public boolean isOnEntityContainer() {
		return getContainer().isEntityContainer();
	}

	@Override
	public final BaseViewContext getParent() {
		return parent;
	}

	@Override
	public <T> PaginatedViewContext<T> paginated() {
		return parent.paginated();
	}

}
