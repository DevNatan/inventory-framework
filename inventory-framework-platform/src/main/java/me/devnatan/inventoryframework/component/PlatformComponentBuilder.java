package me.devnatan.inventoryframework.component;

import java.util.function.Predicate;
import me.devnatan.inventoryframework.context.IFContext;

public abstract class PlatformComponentBuilder<SELF extends ComponentBuilder<SELF>, CONTEXT extends IFContext>
        extends AbstractComponentBuilder<SELF> {

    protected PlatformComponentBuilder() {}

    /**
     * Only shows the component if a given condition is satisfied.
     *
     * @param displayCondition Component display condition.
     * @return This component builder.
     * @see #hideIf(Predicate)
     */
    @SuppressWarnings("unchecked")
    SELF displayIf(Predicate<CONTEXT> displayCondition) {
        this.displayCondition = displayCondition;
        return (SELF) this;
    }

    /**
     * Hides the component if a given condition is satisfied.
     *
     * @param condition Condition to hide the component.
     * @return This component builder.
     * @see #displayIf(Predicate)
     */
    SELF hideIf(Predicate<CONTEXT> condition) {
        return displayIf(condition == null ? null : arg -> !condition.test(arg));
    }
}
