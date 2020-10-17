package me.saiintbrisson.minecraft;

@FunctionalInterface
public interface ViewItemHandler< T> {

    void handle(ViewSlotContext context, T event);

}
