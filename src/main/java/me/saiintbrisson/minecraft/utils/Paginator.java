package me.saiintbrisson.minecraft.utils;

import me.saiintbrisson.minecraft.PaginatedViewContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Paginator<T> {

	private int pageSize;
	private List<T> provider;
	private Function<PaginatedViewContext<T>, Object> lazyProvider;

	@SuppressWarnings("unchecked")
	public Paginator(int pageSize, Object provider) {
		this.pageSize = pageSize;
		if (provider instanceof List) this.provider = (List<T>) provider;
		else if (provider instanceof Function) this.lazyProvider = ( Function<PaginatedViewContext<T>, Object>) provider;
		else throw new IllegalArgumentException("Unsupported pagination source type: " + provider.getClass().getName());
	}

	public List<T> getProvider() {
		return provider;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int size() {
		return provider.size();
	}

	public T get(int index) {
		return provider.get(index);
	}

	public int count() {
		return (int) Math.ceil((double) size() / pageSize);
	}

	public CompletableFuture<List<T>> getPage(int index, PaginatedViewContext<T> context) {
		if (provider != null) return CompletableFuture.completedFuture(getPageBlocking(index));
		if (lazyProvider != null) return getPageLazy(context);

		throw new IllegalStateException(String.format(
			"No source or provider available to fetch page data on index %d.",
			index
		));
	}

	@SuppressWarnings("unchecked")
	private CompletableFuture<List<T>> getPageLazy(PaginatedViewContext<T> context) {
		final Object data = lazyProvider.apply(context);
		if (data instanceof List) {
			final List<T> contents = (List<T>) data;
			return CompletableFuture.completedFuture(contents.isEmpty() ? Collections.emptyList() : new ArrayList<>(contents));
		}

		if (data instanceof CompletableFuture) {
			return ((CompletableFuture<List<T>>) lazyProvider.apply(context));
		}

		throw new IllegalArgumentException(String.format(
			"Pagination provider return value must be a List or CompletableFuture (given %s).",
			data.getClass().getName()
		));
	}

	private List<T> getPageBlocking(int index) {
		if (provider.isEmpty())
			return Collections.emptyList();

		int size = size();

		// fast path
		if (size < pageSize)
			return new ArrayList<>(provider);

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
			", src=" + provider +
			", elements=" + provider.size() +
			", count=" + count() +
			'}';
	}
}