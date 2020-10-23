package me.saiintbrisson.minecraft.utils;

import com.google.common.collect.Lists;

import java.util.*;

public class Paginator<T> {

    private final int pageSize;
    private final List<T> src;

    public Paginator(int pageSize, List<T> src) {
        this.pageSize = pageSize;
        this.src = src;
    }

    public List<T> getSource() {
        return src;
    }

    public int size() {
        return src.size();
    }

    public T get(int index) {
        return src.get(index);
    }

    public int count() {
        return (int) Math.ceil((double) size() / pageSize);
    }

    public int count(int pageSize) {
        return (int) Math.ceil((double) size() / pageSize);
    }

    public Collection<T> getPrevious(int currentIndex) {
        if (!hasPrevious(currentIndex)) {
            throw new NoSuchElementException();
        }

        return getPage(currentIndex - 1);
    }

    public Collection<T> getNext(int currentIndex) {
        if (!hasNext(currentIndex)) {
            throw new NoSuchElementException();
        }

        return getPage(currentIndex + 1);
    }

    public List<T> getPage(int index) {
        if (src.isEmpty())
            return Collections.emptyList();

        int size = size();

        // fast path
        if (size < pageSize)
            return Lists.newArrayList(src);

        if (index < 0 || index >= count())
            throw new ArrayIndexOutOfBoundsException("Index must be between the range of 0 and " + (count() - 1) + ", given: " + index);

        List<T> page = new LinkedList<>();

        int base = index * pageSize;
        int until = base + pageSize;

        if (until > size()) until = size;

        for (int i = base; i < until; i++) {
            page.add(get(i));
        }

        return page;
    }

    public boolean hasPrevious(int index) {
        return count() > 0 && index > 0;
    }

    public boolean hasNext(int index) {
        return index < count() - 1;
    }

    public boolean hasPage(int currentIndex) {
        return currentIndex >= 0 && currentIndex < count();
    }

    @Override
    public String toString() {
        return "Paginator{" +
                "pageSize=" + pageSize +
                ", src=" + src +
                ", elements=" + src.size() +
                ", count=" +  count() +
                '}';
    }
}