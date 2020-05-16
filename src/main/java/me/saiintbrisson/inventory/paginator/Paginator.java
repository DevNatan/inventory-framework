package me.saiintbrisson.inventory.paginator;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
@AllArgsConstructor
public abstract class Paginator<T> {

    private final int pageSize;

    protected abstract List<T> getBackingList();

    public int size() {
        return getBackingList().size();
    }
    public int getPagesCount() {
        return (int) Math.ceil((double) size() / getPageSize());
    }

    public int getPagesCount(int pageSize) {
        return (int) Math.ceil((double) size() / pageSize);
    }

    public T get(int index) {
        return getBackingList().get(index);
    }

    public boolean hasPrevious(int index) {
        return getPagesCount() > 0 && index > 0;
    }
    public Collection<T> previous(int currentIndex) {
        if(!hasPrevious(currentIndex)) {
            throw new NoSuchElementException();
        }

        return getPage(currentIndex - 1);
    }

    public boolean hasNext(int index) {
        return index < getPagesCount() - 1;
    }
    public Collection<T> next(int currentIndex) {
        if(!hasNext(currentIndex)) {
            throw new NoSuchElementException();
        }

        return getPage(currentIndex + 1);
    }

    public boolean hasPage(int currentIndex) {
        return currentIndex >= 0 && currentIndex < getPagesCount();
    }

    public Collection<T> getPage(int index) {
        if(index < 0 || index >= getPagesCount()) {
            throw new ArrayIndexOutOfBoundsException("Index must be between the range of 0 and " + (getPagesCount() - 1));
        }

        List<T> page = new LinkedList<>();

        int base = index * getPageSize();
        int until = base + getPageSize();

        if(until > size()) until = size();

        for(int i = base; i < until; i++) {
            page.add(get(i));
        }

        return page;
    }

}
