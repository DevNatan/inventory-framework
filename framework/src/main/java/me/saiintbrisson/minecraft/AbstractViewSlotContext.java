package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractViewSlotContext extends BaseViewContext implements ViewSlotContext {

	private final BaseViewContext parent;

	@Getter(AccessLevel.PACKAGE)
	private final ViewItem backingItem;

	@Getter
	@Setter
	@ToString.Include
	private boolean cancelled;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.NONE)
	private Object item;

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
	protected ViewItem[] getItems() {
		return parent.getItems();
	}

	@Override
	public final void setItem(@Nullable Object item) {
		this.item = PlatformUtils.getFactory().createItem(item);
	}

	@Override
	public ViewSlotContext ref(String key) {
		ViewItem item = tryResolveRef(this, key);
		if (item == null) item = tryResolveRef(getRoot(), key);
		if (item == null) return null;

		return getRoot().getViewFrame().getFactory().createSlotContext(
			item,
			getRoot(),
			getContainer()
		);
	}

	private ViewItem tryResolveRef(AbstractVirtualView view, String key) {
		for (final ViewItem item : view.getItems()) {
			if (item == null) continue;
			if (item.getReferenceKey() == null) continue;
			if (item.getReferenceKey().equals(key))
				return item;
		}
		return null;
	}

	@Override
	final void inventoryModificationTriggered() {
		throw new IllegalStateException(
			"You cannot modify the inventory directly in the click handler context. " +
				"Use the onRender(...) and then context.setItem(...) instead."
		);
	}

}
