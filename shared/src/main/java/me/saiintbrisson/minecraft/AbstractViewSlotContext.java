package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.format;

@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractViewSlotContext extends BaseViewContext implements ViewSlotContext {

	@Getter(AccessLevel.NONE)
	private final ViewContext parent;

	@Getter(AccessLevel.PACKAGE)
	private final ViewItem backingItem;

	@ToString.Include
	private boolean cancelled;

	@Setter(AccessLevel.NONE)
	private ItemWrapper item;

	private boolean changed;

	AbstractViewSlotContext(ViewItem backingItem, @NotNull final ViewContext parent) {
		super(parent.getRoot(), parent.getContainer());
		this.backingItem = backingItem;
		this.parent = parent;
	}

	@Override
	public final ViewContext getParent() {
		return parent;
	}

	@Override
	public void render(@NotNull ViewContext context) {
		getRoot().render(getParent());
	}

	@Override
	public final void update() {
		// global update cannot be a slot context
		getRoot().update(getParent());
	}

	@Override
	public <T> PaginatedViewContext<T> paginated() {
		return parent.paginated();
	}

	@Override
	public final ViewItem[] getItems() {
		throw new UnsupportedOperationException(
			"Cannot access items from slot context, use parent context instead"
		);
	}

	@Override
	public final boolean hasChanged() {
		return changed;
	}

	public final void setChanged(final boolean changed) {
		this.changed = changed;
	}

	@Override
	@Deprecated
	public final ItemStack getItem() {
		return (ItemStack) getItemWrapper().getValue();
	}

	@Override
	public final @NotNull ItemWrapper getItemWrapper() {
		return item;
	}

	@Override
	public final @NotNull ItemWrapper getCurrentItem() {
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
	public @NotNull ViewSlotContext withItem(@Nullable Object item) {
		setItem(item);
		return this;
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
	public final <T> @NotNull T getItemData(@NotNull String key) {
		return getItemData(key, () -> {
			throw new NoSuchElementException(
				format("Property \"%s\" has not been set for this item. Use #withData(key, value) to set it", key));
		});
	}

	@Override
	public <T> @NotNull T getItemData(@NotNull String key, @NotNull Supplier<T> defaultValue) {
		final Map<String, Object> data = backingItem.getData();
		if (data != null && data.containsKey(key))
			//noinspection unchecked
			return (T) data.get(key);

		return defaultValue.get();
	}

	@Override
	public <T> T getItemDataOrNull(@NotNull String key) {
		return getItemData(key, () -> null);
	}

	@Override
	public final boolean isOnEntityContainer() {
		return getContainer().isEntityContainer();
	}

	@Override
	public final void update(@NotNull ViewContext context) {
		throwNotAllowedCall();
	}

	@Override
	public final void render() {
		throwNotAllowedCall();
	}

	@Override
	public final ViewItem resolve(int index, boolean resolveOnRoot) {
		throwNotAllowedCall();
		return null;
	}

	@Override
	public final String[] getLayout() {
		throwNotAllowedCall();
		return null;
	}

	@Override
	public void setLayout(@Nullable String... layout) {
		throwNotAllowedCall();
	}

	@Override
	public void setLayout(char character, @Nullable Supplier<ViewItem> factory) {
		throwNotAllowedCall();
	}

	@Override
	public void setLayout(char identifier, @Nullable Consumer<ViewItem> layout) {
		throwNotAllowedCall();
	}

	@Override
	public void setLayoutItemsLayer(Stack<Integer> layoutItemsLayer) {
		getParent().setLayoutItemsLayer(layoutItemsLayer);
	}

	@Override
	public void setLayoutSignatureChecked(boolean layoutSignatureChecked) {
		getParent().setLayoutSignatureChecked(layoutSignatureChecked);
	}

	@Override
	public final List<LayoutPattern> getLayoutPatterns() {
		return getParent().getLayoutPatterns();
	}

	@Override
	public final Stack<Integer> getLayoutItemsLayer() {
		return getParent().getLayoutItemsLayer();
	}

	@Override
	public final Deque<ViewItem> getReservedItems() {
		return getParent().getReservedItems();
	}

	@Override
	public final boolean isMarkedToClose() {
		return getParent().isMarkedToClose();
	}

	@Override
	public final void setMarkedToClose(boolean markedToClose) {
		getParent().setMarkedToClose(markedToClose);
	}

	@Override
	public final boolean isPropagateErrors() {
		return getParent().isPropagateErrors();
	}

	@Override
	public final void setPropagateErrors(boolean propagateErrors) {
		getParent().setPropagateErrors(propagateErrors);
	}

	@Override
	int getNextAvailableSlot() {
		throwNotAllowedCall();
		return -1;
	}

	@Contract(value = " -> fail", pure = true)
	private void throwNotAllowedCall() throws RuntimeException {
		throw new RuntimeException("Not allowed to call these kind of method in slot context.");
	}
}
