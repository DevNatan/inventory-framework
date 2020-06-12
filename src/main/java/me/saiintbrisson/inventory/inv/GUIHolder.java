package me.saiintbrisson.inventory.inv;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class GUIHolder implements InventoryHolder {

    private final GUINode node;
    private final UUID id;

    @Setter
    private Inventory inventory;

}
