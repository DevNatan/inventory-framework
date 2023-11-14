package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public abstract class PlatformComponentBuilder<SELF extends ComponentBuilder<SELF>, CONTEXT extends IFContext>
	extends AbstractComponentBuilder<SELF> {

	protected PlatformComponentBuilder(
		Ref<Component> reference,
		Map<String, Object> data,
		boolean cancelOnClick,
		boolean closeOnClick,
		boolean updateOnClick,
		Set<State<?>> watchingStates,
		boolean isManagedExternally,
		Predicate<? extends IFContext> displayCondition
	) {
		super(reference, data, cancelOnClick, closeOnClick, updateOnClick, watchingStates, isManagedExternally, displayCondition);
	}


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
