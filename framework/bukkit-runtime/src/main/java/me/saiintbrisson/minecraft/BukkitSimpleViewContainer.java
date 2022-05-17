package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@ToString
@Getter
@RequiredArgsConstructor
final class BukkitSimpleViewContainer extends BukkitViewContainer {

	@NotNull
	private final Inventory inventory;
	private final ViewType type;

	@Override
	public @NotNull ViewType getType() {
		return type;
	}

	@Override
	public int getRowsCount() {
		return type.getRows();
	}

	@Override
	public int getColumnsCount() {
		return type.getColumns();
	}

}
