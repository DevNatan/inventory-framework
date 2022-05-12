package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@RequiredArgsConstructor
abstract class AbstractVirtualView implements VirtualView {

	private final List<Viewer> viewers = new ArrayList<>();

	void inventoryAccessNeeded() {
	}

}