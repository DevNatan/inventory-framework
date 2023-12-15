package me.devnatan.inventoryframework.component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;

public abstract class AbstractComponentBuilder implements ComponentBuilder {

    private Ref<Component> reference;
    private Map<String, Object> data;
    private boolean cancelOnClick, closeOnClick, updateOnClick;
    private Set<State<?>> watchingStates = new HashSet<>();
    private boolean isSelfManaged;
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

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    protected final boolean isSelfManaged() {
        return isSelfManaged;
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    protected final void setSelfManaged(boolean selfManaged) {
        isSelfManaged = selfManaged;
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
    @ApiStatus.OverrideOnly
    public abstract Component buildComponent(VirtualView root);

    @Override
    public String toString() {
        return "AbstractComponentBuilder{" + "reference="
                + reference + ", data="
                + data + ", cancelOnClick="
                + cancelOnClick + ", closeOnClick="
                + closeOnClick + ", updateOnClick="
                + updateOnClick + ", watchingStates="
                + watchingStates + ", isSelfManaged="
                + isSelfManaged + ", displayCondition="
                + displayCondition + ", key='"
                + key + '\'' + '}';
    }
}
