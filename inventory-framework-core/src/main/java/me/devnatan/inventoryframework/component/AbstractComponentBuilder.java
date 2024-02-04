package me.devnatan.inventoryframework.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public abstract class AbstractComponentBuilder<SELF> implements ComponentBuilder {

    private Ref<Component> reference;
    private Map<String, Object> data;
    private Set<State<?>> watchingStates = new HashSet<>();
    private boolean selfManaged;
    private Predicate<? extends IFContext> displayCondition;
    private String key;

    protected AbstractComponentBuilder() {}

    /**
     * Sets the key of the component.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @return This component builder.
     */
    @ApiStatus.Experimental
    public final SELF key(String key) {
        this.key = key;
        return (SELF) this;
    }

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    public final SELF withSelfManaged(boolean selfManaged) {
        this.selfManaged = selfManaged;
        return (SELF) this;
    }

    /**
     * Watches one or more states.
     * <p>
     * When any of the states provided as parameters are modified, the item generated from this
     * builder will be updated.
     *
     * @param states The state to watch.
     * @return This component builder.
     * @deprecated Use {@link #updateOnStateChange(State)} instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "3.2.0")
    public final SELF watch(State<?>... states) {
        if (watchingStates == null) watchingStates = new LinkedHashSet<>();
        watchingStates.addAll(Arrays.asList(states));
        return (SELF) this;
    }

    /**
     * Listens for value updates in the specified state.
     * <p>
     * Everytime the value of the given state updates, this component will be updated as well.
     *
     * @param state The state to listen changes to.
     * @return This component builder.
     */
    public final SELF updateOnStateChange(@NotNull State<?> state) {
        if (watchingStates == null) watchingStates = new LinkedHashSet<>();
        watchingStates.add(state);
        return (SELF) this;
    }

    /**
     * Listens for value updates in any of the specified states.
     * <p>
     * Everytime the value ANY of the given state updates, this component will be updated as well.
     *
     * @param state The state to listen changes to.
     * @param states Other states to listen changes to.
     * @return This component builder.
     */
    public final SELF updateOnStateChange(@NotNull State<?> state, State<?>... states) {
        if (watchingStates == null) watchingStates = new LinkedHashSet<>();
        watchingStates.add(state);
        watchingStates.addAll(Arrays.asList(states));
        return (SELF) this;
    }

    /**
     * Assigns {@link Ref a reference} to this component.
     *
     * @param reference ComponentPhase reference key.
     * @return This component builder.
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/refs-api">Refs API on Wiki</a>
     */
    public final SELF referencedBy(@NotNull Ref<Component> reference) {
        this.reference = reference;
        return (SELF) this;
    }

    /**
     * Adds a new user-defined property to this component.
     * <p>
     * User-defined properties can be used to persist data that can be retrieved later even after
     * several actions applied to that component.
     * <p>
     * An example of user-defined data persistence is for post-moving identification of a component
     * inside the container, you can define a data in this item and as soon as the actor moves it
     * the data will remain there, and you can use it any way you want.
     *
     * @param key   The property key.
     * @param value The property value.
     * @return This component builder.
     */
    public final SELF withData(@NotNull String key, Object value) {
        if (data == null) data = new HashMap<>();
        data.put(key, value);
        return (SELF) this;
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    protected final boolean isSelfManaged() {
        return selfManaged;
    }

    protected final void setDisplayCondition(Predicate<? extends IFContext> displayCondition) {
        this.displayCondition = displayCondition;
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public void prepareComponent(VirtualView root, AbstractComponent component) {
        component.setRoot(root);
        component.setKey(key);
        component.setReference(reference);
        component.setWatchingStates(watchingStates);
        component.setDisplayCondition(displayCondition);
        component.setSelfManaged(selfManaged);
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public final Component internalBuildComponent(VirtualView root) {
        final Component component = buildComponent(root);
        prepareComponent(root, (AbstractComponent) component);
        return component;
    }

    @Override
    @ApiStatus.OverrideOnly
    public abstract Component buildComponent(VirtualView root);

    @Override
    public String toString() {
        return "AbstractComponentBuilder{" + "reference="
                + reference + ", data="
                + data + ", watchingStates="
                + watchingStates + ", isSelfManaged="
                + selfManaged + ", displayCondition="
                + displayCondition + ", key='"
                + key + '\'' + '}';
    }
}
