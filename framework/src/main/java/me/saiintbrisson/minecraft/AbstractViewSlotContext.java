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
	public final ItemWrapper getItem() {
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
	public final boolean hasChanged() {
		return changed;
	}

	public final void setChanged(final boolean changed) {
		this.changed = changed;
	}

	@Override
	public void updateSlot() {
		getRoot().update(this, getSlot());
	}

	@Override
	public final  <T> T getItemData(@NotNull String key) {
		final Map<String, Object> data = backingItem.getData();
		if (data != null && data.containsKey(key))
			//noinspection unchecked
			return (T) data.get(key);

		throw new NoSuchElementException(format(
			"Property \"%s\" has not been set for this item. Use #withData(key, value) to set it",
			key
		));
	}
}
