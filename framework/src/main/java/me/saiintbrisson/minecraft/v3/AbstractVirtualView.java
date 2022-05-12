package me.saiintbrisson.minecraft.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@Getter
@RequiredArgsConstructor
abstract class AbstractVirtualView implements VirtualView {

	@NotNull
	abstract ViewContainer getContainer();

}