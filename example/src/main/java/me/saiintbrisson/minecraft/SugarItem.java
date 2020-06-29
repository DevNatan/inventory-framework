package me.saiintbrisson.minecraft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.saiintbrisson.minecraft.paginator.PaginatedItem;
import me.saiintbrisson.minecraft.paginator.PaginatedViewHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class SugarItem implements PaginatedItem {

    private final int index;

    @Override
    public ItemStack toItemStack(Player viewer, PaginatedViewHolder holder) {
        return new ItemBuilder(Material.SUGAR)
          .name("§aSugar")
          .amount(index)
          .build();
    }

}
