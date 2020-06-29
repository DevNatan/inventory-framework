package me.saiintbrisson.minecraft.paginator;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PaginatedViewHolder implements InventoryHolder {

    @NonNull
    private final PaginatedView<?> owner;
    private final UUID id;

    @Setter
    private int currentPage;

    @Setter
    private Inventory inventory;

    public void increasePage() {
        currentPage++;
    }

    public void decreasePage() {
        currentPage--;
    }

}
