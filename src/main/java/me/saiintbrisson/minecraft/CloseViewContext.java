package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public final class CloseViewContext extends DelegatedViewContext {

	public CloseViewContext(@NotNull ViewContext delegate) {
		super(delegate);
	}

}
