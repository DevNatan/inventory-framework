package me.saiintbrisson.minecraft.paginator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
@AllArgsConstructor
public abstract class Paginator<T> {

    private final int pageSize;

    protected abstract List<T> getBackingList(Player player);

    public int size(Player player) {
        return getBackingList(player).size();
    }
    public int getPagesCount(Player player) {
        return (int) Math.ceil((double) size(player) / getPageSize());
    }

    public int getPagesCount(Player player, int pageSize) {
        return (int) Math.ceil((double) size(player) / pageSize);
    }

    public T get(Player player, int index) {
        return getBackingList(player).get(index);
    }

    public boolean hasPrevious(Player player, int index) {
        return getPagesCount(player) > 0 && index > 0;
    }
    public Collection<T> previous(Player player, int currentIndex) {
        if(!hasPrevious(player, currentIndex)) {
            throw new NoSuchElementException();
        }

        return getPage(player, currentIndex - 1);
    }

    public boolean hasNext(Player player, int index) {
        return index < getPagesCount(player) - 1;
    }
    public Collection<T> next(Player player, int currentIndex) {
        if(!hasNext(player, currentIndex)) {
            throw new NoSuchElementException();
        }

        return getPage(player, currentIndex + 1);
    }

    public boolean hasPage(Player player, int currentIndex) {
        return currentIndex >= 0 && currentIndex < getPagesCount(player);
    }

    public Collection<T> getPage(Player player, int index) {
        if(index < 0 || index >= getPagesCount(player)) {
            throw new ArrayIndexOutOfBoundsException("Index must be between the range of 0 and " + (getPagesCount(player) - 1));
        }

        List<T> page = new LinkedList<>();

        int base = index * getPageSize();
        int until = base + getPageSize();

        if(until > size(player)) until = size(player);

        for(int i = base; i < until; i++) {
            page.add(get(player, i));
        }

        return page;
    }

}
