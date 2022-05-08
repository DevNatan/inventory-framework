package me.saiintbrisson.minecraft.v3;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@RequiredArgsConstructor
class BaseViewContext extends AbstractVirtualView implements ViewContext {

	@NotNull
	private final ViewContainer container;

}
