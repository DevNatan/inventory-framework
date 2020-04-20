package me.saiintbrisson.inventory.inv;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class InvHolder implements InventoryHolder {

    @NonNull
    private InvNode node;

    @NonNull
    private UUID id;

    @Setter
    private Inventory inventory;

}
