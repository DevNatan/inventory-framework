package me.saiintbrisson.minecraft;

@FunctionalInterface
public interface Handler<T extends ViewContext> {

	void handle(T context);

}