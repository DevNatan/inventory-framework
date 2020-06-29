package me.saiintbrisson.minecraft.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ViewHolder implements InventoryHolder {

    private final ViewNode node;
    private final UUID id;

    @Setter
    private Inventory inventory;

}
