package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AbstractVirtualView implements VirtualView {

	@ToString.Exclude
	@Getter(AccessLevel.PROTECTED)
	private ViewItem[] items;

	private ViewErrorHandler errorHandler;

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
	public void with(@NotNull ViewItem item) {
		throw new UnsupportedOperationException("Items without a defined slot aren't supported yet");
	}

	@Override
	public final void update() {
		throw new UnsupportedOperationException("not available");
	}

	void inventoryModificationTriggered() {
	}

	final void throwException(@NotNull final ViewContext context, @NotNull final Exception exception) {
		if (getErrorHandler() == null)
			return;

		getErrorHandler().error(context, exception);
	}

	void runCatching(@NotNull final ViewContext context, @NotNull final Runnable runnable) {
		// unhandled exception
		if (getErrorHandler() == null) {
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