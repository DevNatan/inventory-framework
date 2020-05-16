package me.saiintbrisson.inventory.paginator;

import lombok.NonNull;
import lombok.Setter;
import me.saiintbrisson.inventory.ItemBuilder;
import me.saiintbrisson.inventory.inv.InvItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PaginatedInv<T extends PaginatedItem> {

    private final Plugin owner;

    private final String title;
    private final int size;

    private final Paginator<T> paginator;

    private InvItem<T>[] items;

    @Setter
    private boolean updateAfterClick = true;

    @Setter
    private BiConsumer<Player, T> itemProcessor;

    public PaginatedInv(@NonNull Plugin owner, String title, int rows, @NonNull Paginator<T> paginator) {
        if(rows < 2 || rows > 6) {
            throw new IllegalArgumentException("Rows must be greater than one and inferior to six");
        }

        if(paginator.getPageSize() < 1 || paginator.getPageSize() >= rows * 9) {
            throw new IllegalArgumentException("Page size must be greater than zero and inferior to rows");
        }

        this.owner = owner;

        this.title = title;
        this.size = rows * 9;

        this.items = new InvItem[9];

        this.paginator = paginator;
    }

    public PaginatedInv(Plugin owner,
                        String title, int rows,
                        int pageSize) {
        this(owner, title, rows, new ListPaginator<>(pageSize));
    }

    public PaginatedInv(Plugin owner,
                        String title, int rows,
                        List<T> list, int pageSize) {
        this(owner, title, rows, new ListPaginator<>(list, pageSize));
    }

    public PaginatedInv(Plugin owner,
                        String title, int rows,
                        int pageSize, Supplier<List<T>> listSupplier) {
        this(owner, title, rows, new Paginator<T>(pageSize) {
            @Override
            protected List<T> getBackingList() {
                return listSupplier.get();
            }
        });
    }

    public void createPreviousItem(String name) {
        items[3] = new InvItem<T>()
          .withItem(new ItemBuilder(Material.ARROW).name(name).build())
          .withSlot(3)
          .onClick((node, event) -> {
              PaginatedInvHolder holder = (PaginatedInvHolder) event.getInventory().getHolder();

              if(!paginator.hasPrevious(holder.getCurrentPage())) return;

              holder.decreasePage();
          });
    }

    public void createNextItem(String name) {
        items[5] = new InvItem<T>()
          .withItem(new ItemBuilder(Material.ARROW).name(name).build())
          .withSlot(5)
          .onClick((node, event) -> {
              PaginatedInvHolder holder = (PaginatedInvHolder) event.getInventory().getHolder();

              if(!paginator.hasNext(holder.getCurrentPage())) return;

              holder.increasePage();
          });
    }

    public void addItem(InvItem<T> item) {
        items[item.getSlot()] = item;
    }

    public boolean updateInventory(Player player, Inventory inventory, PaginatedInvHolder holder) {
        int index = holder.getCurrentPage();
        if(!paginator.hasPage(index)) return false;

        if(buildToInventory(inventory, paginator.getPage(index)) == null) {
            return false;
        }

        player.updateInventory();

        return true;
    }

    public boolean showInventory(Player player) {
        return showInventory(player, 0);
    }

    public boolean showInventory(Player player, int index) {
        if(!paginator.hasPage(index)) return false;

        Inventory inventory = Bukkit.createInventory(
          createHolder(player.getUniqueId()),
          size, title
        );

        if(buildToInventory(inventory, paginator.getPage(index)) == null) {
            return false;
        }

        player.closeInventory();
        player.openInventory(inventory);

        return true;
    }

    public Inventory buildToInventory(Inventory inventory, Collection<T> page) {
        if(inventory == null) return null;

        inventory.clear();

        int current = 0;
        for(T t : page) {
            inventory.setItem(current, t.toItemStack());
            current++;
        }

        int base = size - 9;
        for(int i = 0; i < items.length; i++) {
            InvItem<T> item = items[i];
            if(item == null) continue;

            inventory.setItem(base + i, item.getItemStack());
        }

        return inventory;
    }

    public void handleClick(Plugin plugin, PaginatedInvHolder holder, InventoryClickEvent event) {
        if(!plugin.equals(owner)) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if(slot < 0 ||slot > size) return;

        if(slot >= size - 9) {
            InvItem<T> item = items[slot - (size - 9)];
            if(item == null) return;

            item.handleClick(null, event);

            if(updateAfterClick) {
                updateInventory(((Player) event.getWhoClicked()), event.getInventory(), holder);
            }
            return;
        }

        slot = (paginator.getPageSize() * holder.getCurrentPage()) + slot;

        T t = paginator.get(slot);
        if(t == null) return;

        itemProcessor.accept(((Player) event.getWhoClicked()), t);

        if(updateAfterClick) {
            updateInventory(((Player) event.getWhoClicked()), event.getInventory(), holder);
        }
    }

    public PaginatedInvHolder createHolder(UUID id) {
        return new PaginatedInvHolder(this, id);
    }

}
