package me.saiintbrisson.inventory;

import me.saiintbrisson.inventory.ItemBuilder;
import me.saiintbrisson.inventory.inv.Inv;
import me.saiintbrisson.inventory.inv.InvAction;
import me.saiintbrisson.inventory.inv.InvItem;
import me.saiintbrisson.inventory.inv.InvNode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ExampleInventory extends Inv<Player> {

    private ExampleEmptyInventory inventory;

    public ExampleInventory() {
        super("Example Inventory", 5);

        inventory = new ExampleEmptyInventory();

        setItem(
            new InvItem()
                .withSlot(0, 4)
                .updateOnClick()
                .withItem(
                    new ItemBuilder(Material.BARRIER)
                    .name("§cUpdate")
                    .lore(
                        "",
                        "§eClick here to update"
                    )
                    .amount(3)
                    .build()
                )
        );

        setOpenAction((node, event) -> {
            event.getPlayer().sendMessage("§aYou've opened the inventory");
        });

        setCloseAction((node, event) -> {
            event.getPlayer().sendMessage("§cYou've closed the inventory");
        });
    }

    @Override
    protected void render(InvNode<Player> node, Player player) {
        if(player.hasPermission("inventory")) {
            node.setItem(
                new InvItem()
                .withSlot(2, 4)
                .withItem(trueItem())
                .onClick(trueAction())
            );
        } else {
            node.setItem(
                new InvItem()
                .withSlot(2, 4)
                .withItem(falseItem())
                .cancelClick()
            );
        }

        node.setItem(
            new InvItem()
            .withSlot(4, 4)
            .withItem(emptyItem())
            .openInv(inventory, player)
        );
    }

    private InvAction<InventoryClickEvent> trueAction() {
        return (node, event) -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage("§a§lYay!");
        };
    }

    private ItemStack falseItem() {
        return new ItemBuilder(Material.WOOL)
                .durability((short) 14)
                .name("§cYou don't have enough permissions :/")
                .lore("", "§7You need 'inventory' permission")
                .build();
    }

    private ItemStack trueItem() {
        return new ItemBuilder(Material.WOOL)
                .durability((short) 5)
                .name("§aNice!")
                .lore("", "§eClick here to execute a function")
                .build();
    }

    private ItemStack emptyItem() {
        return new ItemBuilder(Material.STAINED_GLASS_PANE)
                .durability((short) 7)
                .name("§cEmpty")
                .lore("", "§eClick to open an empty inventory")
                .build();
    }

}
