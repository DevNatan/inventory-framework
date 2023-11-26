package me.devnatan.inventoryframework.context;

import java.util.Objects;
import me.devnatan.inventoryframework.RootView;

public final class EndlessContextInfo {

    private final String contextId;
    private final RootView view;
    private boolean invalidated;

    public EndlessContextInfo(String contextId, RootView view) {
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
        getView().invalidateEndlessContext(contextId);
        invalidated = true;
    }

    public boolean wasInvalidated() {
        return invalidated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndlessContextInfo that = (EndlessContextInfo) o;
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
