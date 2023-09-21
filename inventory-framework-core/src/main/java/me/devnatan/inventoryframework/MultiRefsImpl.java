package me.devnatan.inventoryframework;

import java.util.ArrayList;
import java.util.List;
import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;

final class MultiRefsImpl<E> implements Ref<List<E>> {

    private final List<E> assignments = new ArrayList<>();

    @Override
    public @NotNull List<E> value(@NotNull IFContext context) {
        return assignments;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void assign(Object value) {
        synchronized (assignments) {
            try {
                assignments.add((E) value);
            } catch (final ClassCastException exception) {
                throw new IllegalArgumentException(
                        "Multi-Refs assignment type must be the same as type parameter type", exception);
            }
        }
    }
}
