package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@ToString
@Getter
@RequiredArgsConstructor
final class BukkitChestViewContainer extends BukkitViewContainer {

    private static final int COLUMNS = 9;

    @NotNull
    private final Inventory inventory;

    @Override
    public @NotNull ViewType getType() {
        return ViewType.CHEST;
    }

    @Override
    public int getRowsCount() {
        return getSize() / COLUMNS;
    }

    @Override
    public int getColumnsCount() {
        return COLUMNS;
    }
}
