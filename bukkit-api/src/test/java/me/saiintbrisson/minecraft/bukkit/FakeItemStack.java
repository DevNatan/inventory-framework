package me.saiintbrisson.minecraft.bukkit;

import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString
public class FakeItemStack extends ItemStack {

    private Material type;
    private short durability;

    public FakeItemStack(Material type) {
        super(type, 1, (short) 0);
        setType(type);
    }

    public FakeItemStack(ItemStack item) {
        super(item.getType(), item.getAmount(), item.getDurability());
        if (!(item instanceof FakeItemStack)) throw new IllegalStateException("ItemStack must be a FakeItemStack");
        setType(item.getType());
    }

    @NotNull
    @Override
    public final Material getType() {
        return type;
    }

    @Override
    public final void setType(@NotNull Material type) {
        this.type = type;
    }

    @Override
    public final short getDurability() {
        return durability;
    }

    @Override
    public final void setDurability(short durability) {
        this.durability = durability;
    }

    @Nullable
    @Override
    public final ItemMeta getItemMeta() {
        return null;
    }

    @Override
    public final boolean setItemMeta(@Nullable ItemMeta itemMeta) {
        return false;
    }

    @Override
    public boolean isSimilar(@Nullable ItemStack stack) {
        if (stack == null) return false;
        if (stack == this) return true;

        return getType() == stack.getType() && getDurability() == stack.getDurability();
    }
}
