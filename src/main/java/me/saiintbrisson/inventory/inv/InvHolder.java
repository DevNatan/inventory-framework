package me.saiintbrisson.inventory.inv;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class InvHolder implements InventoryHolder {

    private final InvNode node;
    private final UUID id;

    @Setter
    private Inventory inventory;

}
