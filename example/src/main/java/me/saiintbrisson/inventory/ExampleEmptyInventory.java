package me.saiintbrisson.inventory;

import me.saiintbrisson.inventory.ItemBuilder;
import me.saiintbrisson.inventory.inv.Inv;
import me.saiintbrisson.inventory.inv.InvItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ExampleEmptyInventory extends Inv<Player> {

    public ExampleEmptyInventory() {
        super("Example Inventory", 3);

        setItem(
            new InvItem()
                .withSlot(1, 4)
                .closeOnClick()
                .withItem(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .durability((short) 7)
                    .name("§7Empty")
                    .build()
                )
        );
    }

}
