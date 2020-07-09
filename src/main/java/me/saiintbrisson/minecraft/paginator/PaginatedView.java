package me.saiintbrisson.minecraft.paginator;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.saiintbrisson.minecraft.ItemBuilder;
import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.view.ViewItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class PaginatedView<T extends PaginatedItem> implements View {

    private final Plugin owner;

    @Getter
    private final String title;
    private final int size;

    private int[] slotsIndex;
    private final Paginator<T> paginator;

    private final ViewItem<T>[] items;
    @Getter
    @Setter
    private ViewItem<T> previousButton, nextButton;

    @Setter
    private boolean updateAfterClick = true;

    @Setter
    private PaginatedItemConsumer<T> itemProcessor;

    public PaginatedView(@NonNull Plugin owner, String title, int rows,
                         @NonNull Paginator<T> paginator) {
        if(rows < 2 || rows > 6) {
            throw new IllegalArgumentException("Rows must be greater than one and inferior to six");
        }

        if(paginator.getPageSize() < 1 || paginator.getPageSize() >= rows * 9) {
            throw new IllegalArgumentException("Page size must be greater than zero and inferior to rows");
        }

        this.owner = owner;

        this.title = title;
        this.size = rows * 9;

        this.items = new ViewItem[size];

        this.paginator = paginator;

        previousButton = new ViewItem<T>()
          .withItem(new ItemBuilder(Material.ARROW).name("§aPrevious page").build())
          .withSlot(size - 9 + 3)
          .onClick((node, player, event) -> {
              ((PaginatedViewHolder) event.getInventory().getHolder()).decreasePage();
          });

        nextButton = new ViewItem<T>()
          .withItem(new ItemBuilder(Material.ARROW).name("§aNext page").build())
          .withSlot(size - 9 + 5)
          .onClick((node, player, event) -> {
              ((PaginatedViewHolder) event.getInventory().getHolder()).increasePage();
          });
    }

    public PaginatedView(Plugin owner,
                         String title, String[] layout,
                         Function<Player, List<T>> listFunction) {
        int length = layout.length;
        if(length < 2 || length > 6) {
            throw new IllegalArgumentException("Layout rows must be greater than one and inferior to seven");
        }

        int pageSize = 0;

        this.slotsIndex = new int[length * 9];

        int previousIndex = -1;
        int nextIndex = -1;

        for(int i = 0; i < layout.length; i++) {
            String s = layout[i];
            char[] chars = s.toCharArray();

            if(chars.length != 9) {
                throw new IllegalArgumentException("All layout lines must have nine characters");
            }

            for(int charIndex = 0; charIndex < chars.length; charIndex++) {
                char c = chars[charIndex];
                int index = i * 9 + charIndex;
                slotsIndex[index] = -1;

                if(c == 'O' || c == '0') {
                    continue;
                }

                if(c == 'X' || c == '1') {
                    slotsIndex[index] = pageSize;
                    pageSize++;
                } else if(c == '<') {
                    if(previousIndex != -1) {
                        throw new IllegalArgumentException("A page can have only one previous button");
                    }

                    previousIndex = index;
                } else if(c == '>') {
                    if(nextIndex != -1) {
                        throw new IllegalArgumentException("A page can have only one next button");
                    }

                    nextIndex = index;
                } else {
                    throw new IllegalArgumentException("Malformed layout near " + c
                      + " (" + index + ")");
                }
            }
        }

        if(pageSize == 0) {
            throw new IllegalArgumentException("Page size must be grater than zero");
        }

        this.owner = owner;

        this.title = title;
        this.size = slotsIndex.length;

        this.items = new ViewItem[size];

        this.paginator = new Paginator<T>(pageSize) {
            @Override
            protected List<T> getBackingList(Player player) {
                return listFunction.apply(player);
            }
        };

        if(previousIndex != -1) {
            previousButton = new ViewItem<T>()
              .withItem(new ItemBuilder(Material.ARROW).name("§aPrevious page").build())
              .withSlot(previousIndex)
              .onClick((node, player, event) -> {
                  ((PaginatedViewHolder) event.getInventory().getHolder()).decreasePage();
              });
        }

        if(nextIndex != -1) {
            nextButton = new ViewItem<T>()
              .withItem(new ItemBuilder(Material.ARROW).name("§aNext page").build())
              .withSlot(nextIndex)
              .onClick((node, player, event) -> {
                  ((PaginatedViewHolder) event.getInventory().getHolder()).increasePage();
              });
        }
    }

    public PaginatedView(Plugin owner, String title, int rows,
                         int pageSize, Function<Player, List<T>> listFunction) {
        this(owner, title, rows, new Paginator<T>(pageSize) {
            @Override
            protected List<T> getBackingList(Player player) {
                return listFunction.apply(player);
            }
        });
    }

    @Override
    public int getRows() {
        return size / 9;
    }

    public void addItem(ViewItem<T> item) {
        if(slotsIndex != null && slotsIndex[item.getSlot()] != -1) {
            throw new IllegalArgumentException("Slot " + item.getSlot() + " must be reserved");
        }

        items[item.getSlot()] = item;
    }

    public boolean updateInventory(Player player, Inventory inventory, PaginatedViewHolder holder) {
        int index = holder.getCurrentPage();

        if(!paginator.hasPage(player, index)) {
            if(!paginator.hasPrevious(player, index)) return false;
            holder.decreasePage();
            index--;
        }

        Collection<T> page = paginator.getPage(player, index);

        if(buildToInventory(player, inventory, page) == null) {
            return false;
        }

        player.updateInventory();

        return true;
    }

    public boolean showInventory(Player player) {
        return showInventory(player, 0);
    }

    public boolean showInventory(Player player, int index) {
        if(!paginator.hasPage(player, index)) return false;

        Inventory inventory = Bukkit.createInventory(
          createHolder(player.getUniqueId()),
          size, title
        );

        if(buildToInventory(player, inventory, paginator.getPage(player, index)) == null) {
            return false;
        }

        player.closeInventory();
        player.openInventory(inventory);

        return true;
    }

    public Inventory buildToInventory(Player player, Inventory inventory, Collection<T> page) {
        if(inventory == null) return null;

        inventory.clear();

        PaginatedViewHolder holder = (PaginatedViewHolder) inventory.getHolder();

        int current = 0;
        if(slotsIndex != null) {
            Iterator<T> iterator = page.iterator();
            for(int slot : slotsIndex) {
                if(!iterator.hasNext()) {
                    break;
                }

                if(slot == -1) {
                    current++;
                    continue;
                }

                inventory.setItem(current, iterator.next().toItemStack(player, holder));
                current++;
            }
        } else {
            for(T t : page) {
                inventory.setItem(current, t.toItemStack(player, holder));
                current++;
            }
        }

        for(ViewItem<T> item : items) {
            if(item == null) continue;
            inventory.setItem(item.getSlot(), item.getItemStack());
        }

        if(previousButton != null && paginator.hasPrevious(player, holder.getCurrentPage())) {
            inventory.setItem(previousButton.getSlot(), previousButton.getItemStack());
        }

        if(nextButton != null && paginator.hasNext(player, holder.getCurrentPage())) {
            inventory.setItem(nextButton.getSlot(), nextButton.getItemStack());
        }

        return inventory;
    }

    public void handleClick(Plugin plugin, PaginatedViewHolder holder, InventoryClickEvent event) {
        if(!plugin.equals(owner)) return;

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if(slot < 0 || slot > size) return;

        if(previousButton != null && previousButton.getSlot() == slot && paginator.hasPrevious(player, holder.getCurrentPage())) {
            previousButton.handleClick(null, event);
            updateInventory(player, event.getInventory(), holder);
            return;
        }

        if(nextButton != null && nextButton.getSlot() == slot && paginator.hasNext(player, holder.getCurrentPage())) {
            nextButton.handleClick(null, event);
            updateInventory(player, event.getInventory(), holder);
            return;
        }

        ViewItem<T> item = items[slot];
        if(item != null) {
            item.handleClick(null, event);

            if(updateAfterClick) {
                updateInventory(player, event.getInventory(), holder);
            }
            return;
        }

        if(slotsIndex != null) {
            if(slotsIndex[slot] == -1) return;

            slot = (paginator.getPageSize() * holder.getCurrentPage()) + slotsIndex[slot];
        } else {
            slot = (paginator.getPageSize() * holder.getCurrentPage()) + slot;
        }

        if(slot >= paginator.size(player)) return;

        T t = paginator.get(player,slot);
        if(t == null) return;

        if (itemProcessor != null) {
            itemProcessor.process(player, t, event);
        }

        if(updateAfterClick) {
            updateInventory(player, event.getInventory(), holder);
        }
    }

    public PaginatedViewHolder createHolder(UUID id) {
        return new PaginatedViewHolder(this, id);
    }

}
