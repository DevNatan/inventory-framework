package me.saiintbrisson.inventory.paginator;

import lombok.Getter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Getter
public class ListPaginator<T> extends Paginator<T> {

    private final List<T> backingList = new LinkedList<>();

    public ListPaginator(Collection<? extends T> c, int pageSize) {
        super(pageSize);
        backingList.addAll(c);
    }

    public ListPaginator(int pageSize) {
        super(pageSize);
    }

}
