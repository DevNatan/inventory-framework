package me.saiintbrisson.minecraft;

import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString(callSuper = true)
final class ViewContextImpl extends BaseViewContext {

	public ViewContextImpl(@NotNull View view, @Nullable ViewContainer container) {
		super(view, container);
	}

}
