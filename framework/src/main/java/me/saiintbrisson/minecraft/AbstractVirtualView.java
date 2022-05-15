package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AbstractVirtualView implements VirtualView {

	private ViewErrorHandler errorHandler;

	@Override
	@NotNull
	public final ViewItem item() {
		throw new UnsupportedOperationException("not available");
	}

	@Override
	@NotNull
	public final ViewItem item(@NotNull Object item) {
		throw new UnsupportedOperationException("not available");
	}

	@Override
	@NotNull
	public final ViewItem slot(int slot) {
		throw new UnsupportedOperationException("not available");
	}

	@Override
	@NotNull
	public final ViewItem slot(int slot, Object item) {
		throw new UnsupportedOperationException("not available");
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
		throw new UnsupportedOperationException("not available");
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