package me.saiintbrisson.minecraft;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Stores user context history and the currently active context. */
@Getter
@ToString
@RequiredArgsConstructor
final class ViewContextStore {

    private static final int DEFAULT_DEPTH = 5;

    private ViewContext active;

    private final int depth;

    @Getter(AccessLevel.NONE)
    private final Stack<ViewContext> history = new Stack<>();

    public ViewContextStore(@NotNull ViewContext active) {
        this(DEFAULT_DEPTH);
        this.active = active;
    }

    /**
     * Sets the parameter context to the new active context and adds the context that was previously
     * active to the context history.
     *
     * @param context The context to be added.
     * @return The context parameter.
     */
    @Contract("_ -> param1")
    public ViewContext updateActive(@NotNull ViewContext context) {
        if (active != null) addHistory(context);
        this.active = context;
        return context;
    }

    /**
     * Gets the last context in the history.
     *
     * @return The last context in the contexts stack.
     */
    @Nullable
    public ViewContext getLast() {
        try {
            return history.lastElement();
        } catch (final NoSuchElementException | IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    /**
     * Adds a new context to the history removing the first history context if the depth is reached.
     *
     * @param context The context to be added.
     */
    private void addHistory(@NotNull ViewContext context) {
        if (history.size() == depth) history.remove(0);

        history.add(context);
    }

    /** Clears the context history. */
    public void clear() {
        history.clear();
    }

    /**
     * Clears the context history in addition to invalidating them and invalidating the currently
     * active context as well.
     */
    public void invalidate() {
        //noinspection StatementWithEmptyBody
        if (active != null) {
            // TODO invalidate currently active context
        }

        final Iterator<ViewContext> iterator = history.iterator();
        while (iterator.hasNext()) {
            // TODO invalidate context
            iterator.remove();
        }
    }
}
