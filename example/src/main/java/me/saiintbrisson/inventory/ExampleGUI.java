package me.saiintbrisson.inventory.example;

import lombok.Getter;
import lombok.NonNull;
import me.saiintbrisson.inventory.ItemBuilder;
import me.saiintbrisson.inventory.inv.GUI;
import me.saiintbrisson.inventory.inv.GUIAction;
import me.saiintbrisson.inventory.inv.GUIItem;
import me.saiintbrisson.inventory.inv.GUINode;
import me.saiintbrisson.inventory.paginator.PaginatedView;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ExampleGUI extends GUI<ExampleObject> {

    @Getter
    private final List<SugarItem> sugarList;
    private final PaginatedView<SugarItem> view;

    public ExampleGUI(@NonNull Plugin owner) {
        super(owner, "Example Inventory", 5);

        appendItem(new GUIItem<ExampleObject>()
          .withSlot(0, 4)
          .withItem(new ItemBuilder(Material.BARRIER)
            .name("§cUpdate")
            .lore("", "§eClick here to update")
            .amount(3)
            .build())
          .updateOnClick()
        );

        setOpenAction((node, player, event) -> {
            player.sendMessage("§aYou've opened the inventory");
        });

        setCloseAction((node, player, event) -> {
            player.sendMessage("§cYou've closed the inventory");
        });

        sugarList = new ArrayList<>();
        for (int i = 0; i < 64; ) {
            sugarList.add(new SugarItem(++i));
        }

        view = new PaginatedView<>(owner, "Sugars", new String[]{
          "OOOOOOOOO",
          "OXXXXXXXO",
          "OOOOOOOOO",
          "OOO<O>OOO",
        }, this::getSugarList);

        view.setItemProcessor((player, sugarItem) -> {
            player.sendMessage("§aSelected sugar: §f" + sugarItem.getIndex());
            player.closeInventory();
        });
    }

    @Override
    protected void render(GUINode<ExampleObject> node, ExampleObject object) {
        Player player = node.getPlayer();

        if (player.isOp()) {

            node.appendItem(
              new GUIItem<ExampleObject>()
                .withSlot(2, 4)
                .withItem(trueItem())
                .onClick(trueAction())
            );

        } else {

            node.appendItem(
              new GUIItem<ExampleObject>()
                .withSlot(2, 4)
                .withItem(falseItem())
                .cancelClick()
                .playSoundOnClick(Sound.ANVIL_LAND, 1, 1)
            );

        }

        node.appendItem(
          new GUIItem<ExampleObject>()
            .withSlot(4, 3)
            .withItem(new ItemBuilder(Material.PAPER)
              .name("§dObject information:")
              .lore("",
                "  §aName: §f" + object.getName(),
                "  §aEmail: §f" + object.getEmail(),
                "  §aAge: §f" + object.getAge(),
                "", "§7§oClick to select")
              .build())
            .messageOnClick("§aSelected §f%s§a.", object.getName())
            .closeOnClick()
        );

        node.appendItem(
          new GUIItem<ExampleObject>()
            .withSlot(4, 5)
            .withItem(paginatorItem())
            .openPaginatedView(view)
        );

    }

    private GUIAction<ExampleObject, InventoryClickEvent> trueAction() {
        return (node, player, event) -> {
            event.setCancelled(true);
            player.closeInventory();
            player.sendMessage("§a§lYay!");
        };
    }

    private ItemStack falseItem() {
        return new ItemBuilder(Material.WOOL)
          .durability((short) 14)
          .name("§cYou don't have enough permissions :/")
          .lore("", "§7Operator role needed")
          .build();
    }

    private ItemStack trueItem() {
        return new ItemBuilder(Material.WOOL)
          .durability((short) 5)
          .name("§aNice!")
          .lore("", "§eClick here to execute a function")
          .build();
    }

    private ItemStack paginatorItem() {
        return new ItemBuilder(Material.STAINED_GLASS_PANE)
          .durability((short) 7)
          .name("§6Paginator Example")
          .lore("", "§eClick to open a paginated inventory")
          .build();
    }

}
