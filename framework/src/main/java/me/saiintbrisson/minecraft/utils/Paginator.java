package me.saiintbrisson.minecraft.utils;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Paginator<T> {

    private int pageSize;
    private final List<T> source;

    public Paginator(int pageSize, List<T> source) {
        this.pageSize = pageSize;
        this.source = source;
    }

	public List<T> getSource() {
		return source;
	}

	public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int size() {
        return source.size();
    }

    public T get(int index) {
        return source.get(index);
    }

    public int count() {
        return (int) Math.ceil((double) size() / pageSize);
    }

    public List<T> getPage(int index) {
        if (source.isEmpty())
            return Collections.emptyList();

        int size = size();

        // fast path
        if (size < pageSize)
            return Lists.newArrayList(source);

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

    public boolean hasPage(int currentIndex) {
        return currentIndex >= 0 && currentIndex < count();
    }

    @Override
    public String toString() {
        return "Paginator{" +
                "pageSize=" + pageSize +
                ", src=" + source +
                ", elements=" + source.size() +
                ", count=" + count() +
                '}';
    }
}