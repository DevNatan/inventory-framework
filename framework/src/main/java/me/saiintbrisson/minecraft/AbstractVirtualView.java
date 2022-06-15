package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AbstractVirtualView implements VirtualView {

	@ToString.Exclude
	private ViewItem[] items;

	@Setter(AccessLevel.PACKAGE)
	private ViewErrorHandler errorHandler;

	protected ViewItem[] getItems() {
		return items;
	}

	final void setItems(ViewItem[] items) {
		this.items = items;
	}

	public final ViewErrorHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	@NotNull
	public final ViewItem slot(int slot) {
		return Objects.requireNonNull(
			slot(slot, null),
			"ViewItem cannot be null"
		);
	}

	@Override
	@NotNull
	public final ViewItem slot(int slot, Object item) {
		if (getItems() == null)
			throw new IllegalStateException("VirtualView was not initialized yet");

		final ViewItem viewItem = new ViewItem(slot);
		viewItem.setItem(item);
		getItems()[slot] = viewItem;
		return viewItem;
	}

	@Override
	@NotNull
	public final ViewItem slot(int row, int column) {
		throw new UnsupportedOperationException("not available");
	}

	@Override
	@NotNull
	public final ViewItem slot(int row, int column, Object item) {
		throw new UnsupportedOperationException("not available");
	}

	@Override
	public final void with(@NotNull ViewItem item) {
		throw new UnsupportedOperationException("Items without a defined slot aren't supported yet");
	}

	protected void render(@NotNull ViewContext context) {
		inventoryModificationTriggered();

		for (int i = 0; i < getItems().length; i++) {
			render(context, i);
		}
	}

	protected final void render(@NotNull ViewContext context, int slot) {
		final ViewItem item = context.resolve(slot, true);
		if (item == null)
			return;

		render(context, item, slot);
	}

	private void render(
		@NotNull ViewContext context,
		@NotNull ViewItem item,
		int slot
	) {
		final Object fallbackItem = item.getItem();

		if (item.getRenderHandler() != null) {
			final ViewSlotContext renderContext = PlatformUtils.getFactory()
				.createSlotContext(item, context.getRoot(), context.getContainer());

			runCatching(context, () -> item.getRenderHandler().handle(renderContext));
			if (renderContext.hasChanged()) {
				context.getContainer().renderItem(slot, renderContext.getItem());
				renderContext.setChanged(false);
				return;
			}
		}

		if (fallbackItem == null)
			throw new IllegalArgumentException(String.format(
				"No item were provided and the rendering function was not defined at slot %d." +
					"You must use a rendering function slot(...).onRender(...)" +
					" or a fallback item slot(fallbackItem)",
				slot
			));

		context.getContainer().renderItem(slot, fallbackItem);
	}

	@Override
	public final void update() {
		inventoryModificationTriggered();
		throw new UnsupportedOperationException("Update aren't supported in this view");
	}

	void update(@NotNull ViewContext context) {
		for (int i = 0; i < getItems().length; i++)
			update(context, i);
	}

	final void update(@NotNull ViewContext context, int slot) {
		final ViewItem item = context.resolve(slot, true);
		if (item == null) {
			context.getContainer().removeItem(slot);
			return;
		}

		if (item.getUpdateHandler() != null) {
			final ViewSlotContext updateContext = PlatformUtils.getFactory()
				.createSlotContext(item, context.getRoot(), context.getContainer());

			runCatching(context, () -> item.getUpdateHandler().handle(updateContext));
			if (updateContext.hasChanged()) {
				context.getContainer().renderItem(slot, updateContext.getItem());
				updateContext.setChanged(false);
				return;
			}
		}

		// update handler can be used as a empty function, so we fall back to the render handler to
		// update the fallback item properly
		render(context, item, slot);
	}

	ViewItem resolve(int index) {
		// fast path -- skip -999 index on some platforms
		if (index < 0) return null;

		final int len = getItems().length;
		if (index >= len)
			return null;

		return getItems()[index];
	}

	void inventoryModificationTriggered() {
	}

	final void throwException(@NotNull final ViewContext context, @NotNull final Exception exception) {
		ViewErrorHandler errorHandler = getErrorHandler();
		if (errorHandler == null) errorHandler = context.getErrorHandler();
		if (errorHandler == null) return;

		errorHandler.error(context, exception);
	}

	final void runCatching(@NotNull final ViewContext context, @NotNull final Runnable runnable) {
		// unhandled exception
		if (getErrorHandler() == null && context.getErrorHandler() == null) {
			runnable.run();
			return;
		}

		try {
			runnable.run();
		} catch (final Exception e) {
			throwException(context, e);
		}
	}

}