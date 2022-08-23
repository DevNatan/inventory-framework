package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@ToString
@Getter
@RequiredArgsConstructor
final class BukkitEntityViewContainer extends BukkitViewContainer {

    private static final int SIZE = InventoryType.PLAYER.getDefaultSize();
    private static final int ROWS = 4;
    private static final int COLUMNS = 9;

    @NotNull
    private final Inventory inventory;

    @Override
    public @NotNull ViewType getType() {
        return new ViewType("player", SIZE, ROWS, COLUMNS, false) {
            @Override
            public boolean canPlayerInteractOn(int slot) {
                return true;
            }
        };
    }

    @Override
    public int getRowsCount() {
        return ROWS;
    }

    @Override
    public int getColumnsCount() {
        return COLUMNS;
    }

    @Override
    public boolean isEntityContainer() {
        return true;
    }
}
