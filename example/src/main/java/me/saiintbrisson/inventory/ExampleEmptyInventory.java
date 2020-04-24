package me.saiintbrisson.inventory;

import me.saiintbrisson.inventory.inv.Inv;
import me.saiintbrisson.inventory.inv.InvItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ExampleEmptyInventory extends Inv<Player> {

    public ExampleEmptyInventory(Plugin plugin) {
        super(plugin, "Example Inventory", 3);

        setItem(
          new InvItem<Player>()
            .withSlot(1, 4)
            .closeOnClick()
            .messageOnClick("§cClosed.")
            .playSoundOnClick(Sound.ANVIL_BREAK, 1, 1)
            .withItem(
              new ItemBuilder(Material.STAINED_GLASS_PANE)
                .durability((short) 7)
                .name("§7Empty")
                .lore("", "§7Clicking here will close this inventory.")
                .build()
            )
        );
    }

}
