package me.devnatan.inventoryframework.component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;

public abstract class AbstractComponentBuilder implements ComponentBuilder {

    private Ref<Component> reference;
    private Map<String, Object> data;
    private boolean cancelOnClick, closeOnClick, updateOnClick;
    private Set<State<?>> watchingStates = new HashSet<>();
    private boolean isManagedExternally;
    private Predicate<? extends IFContext> displayCondition;
    private String key;

    protected AbstractComponentBuilder() {}

    protected final Ref<Component> getReference() {
        return reference;
    }

    protected final void setReference(Ref<Component> reference) {
        this.reference = reference;
    }

    protected final Map<String, Object> getData() {
        return data;
    }

    protected final void setData(Map<String, Object> data) {
        this.data = data;
    }

    protected final boolean isCancelOnClick() {
        return cancelOnClick;
    }

    protected final void setCancelOnClick(boolean cancelOnClick) {
        this.cancelOnClick = cancelOnClick;
    }

    protected final boolean isCloseOnClick() {
        return closeOnClick;
    }

    protected final void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
    }

    protected final boolean isUpdateOnClick() {
        return updateOnClick;
    }

    protected final void setUpdateOnClick(boolean updateOnClick) {
        this.updateOnClick = updateOnClick;
    }

    protected final Set<State<?>> getWatchingStates() {
        return watchingStates;
    }

    protected final void setWatchingStates(Set<State<?>> watchingStates) {
        this.watchingStates = watchingStates;
    }

    protected final boolean isManagedExternally() {
        return isManagedExternally;
    }

    protected final void setManagedExternally(boolean managedExternally) {
        isManagedExternally = managedExternally;
    }

    protected final Predicate<? extends IFContext> getDisplayCondition() {
        return displayCondition;
    }

    protected final void setDisplayCondition(Predicate<? extends IFContext> displayCondition) {
        this.displayCondition = displayCondition;
    }

    protected final String getKey() {
        return key;
    }

    protected final void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "AbstractComponentBuilder{" + "reference="
                + reference + ", data="
                + data + ", cancelOnClick="
                + cancelOnClick + ", closeOnClick="
                + closeOnClick + ", updateOnClick="
                + updateOnClick + ", watchingStates="
                + watchingStates + ", isManagedExternally="
                + isManagedExternally + ", displayCondition="
                + displayCondition + ", key='"
                + key + '\'' + '}';
    }
}
