package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
abstract class AbstractVirtualView implements VirtualView {

	private ViewErrorHandler errorHandler;

	void inventoryModificationTriggered() {
	}

	final void throwException(@NotNull ViewContext context, @NotNull Exception exception) {
		if (getErrorHandler() == null)
			return;

		getErrorHandler().error(context, exception);
	}

	void runCatching(@NotNull ViewContext context, @NotNull Runnable runnable) {
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