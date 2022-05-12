package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Context created for when the container is closed, it is possible to cancel it so that the
 * container is not closed.
 */
@ToString(callSuper = true)
public final class CloseViewContext extends BaseViewContext {

	@Getter
	@Setter
	private boolean cancelled;

	CloseViewContext(@NotNull ViewContext context) {
		super(context);
	}

}
