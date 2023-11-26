package me.devnatan.inventoryframework.context;

import java.util.Objects;
import me.devnatan.inventoryframework.RootView;

public final class EndlessContextData {

    private final String contextId;
    private final RootView view;

    public EndlessContextData(String contextId, RootView view) {
        this.contextId = contextId;
        this.view = view;
    }

    public String getContextId() {
        return contextId;
    }

    public RootView getView() {
        return view;
    }

    public void invalidate() {
        getView().invalidateContext(contextId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndlessContextData that = (EndlessContextData) o;
        return Objects.equals(contextId, that.contextId) && Objects.equals(view, that.view);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextId, view);
    }

    @Override
    public String toString() {
        return "EndlessContextData{" + "contextId='" + contextId + '\'' + ", view=" + view + '}';
    }
}
