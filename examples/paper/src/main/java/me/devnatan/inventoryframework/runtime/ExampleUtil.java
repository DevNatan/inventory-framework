package me.devnatan.inventoryframework.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ExampleUtil {

    public static List<ItemStack> getRandomItems(int amount) {

        Material[] materials = Material.values();
        Random random = new Random();

        ArrayList<ItemStack> result = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            result.add(new ItemStack(materials[random.nextInt(10, 100)]));
        }

        return result;
    }

    public static ItemStack displayItem(Material material, String displayName) {
        return displayItem(material, displayName, 1);
    }

    public static ItemStack displayItem(Material material, String displayName, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        item.setItemMeta(itemMeta);
        return item;
    }
}
