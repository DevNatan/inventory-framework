package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.UnsupportedOperationInSharedContextException;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractComponent implements Component {

	private final String defaultKey = UUID.randomUUID().toString();
	private final VirtualView root;
	private final Ref<Component> reference;
	private final Set<State<?>> watchingStates;
	private boolean isVisible;

	protected AbstractComponent() {
		this(null, null, null);
	}

	protected AbstractComponent(VirtualView root,
					  Ref<Component> reference,
					  Set<State<?>> watchingStates) {
		this.root = root;
		this.reference = reference;
		this.watchingStates = watchingStates;
	}

	@Override
	public String getKey() {
		return defaultKey;
	}

	@Override
	public final @NotNull VirtualView getRoot() {
		return root;
	}

	@Override
	public final boolean intersects(@NotNull Component other) {
		return Component.intersects(this, other);
	}

	@Override
	public final InteractionHandler getInteractionHandler() {
		return null;
	}

	@Override
	public final @UnmodifiableView Set<State<?>> getWatchingStates() {
		return Collections.unmodifiableSet(watchingStates);
	}

	@Override
	public final boolean isVisible() {
		return isVisible;
	}

	@Override
	public final void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	@Override
	public final boolean isManagedExternally() {
		// TODO remove this from API
		return false;
	}

	@Override
	public final boolean shouldRender(IFContext context) {
		throw new UnsupportedOperationInSharedContextException();
	}

	@Override
	public final void update() {
		if (isManagedExternally())
			throw new IllegalStateException(
				"This component is externally managed by another component and cannot be updated directly");

		getRootAsContext().updateComponent(this, false);
	}

	@Override
	public final Ref<Component> getReference() {
		return reference;
	}

	@Override
	public final void forceUpdate() {
		getRootAsContext().updateComponent(this, true);
	}

	@Override
	public final void show() {
		setVisible(true);
		update();
	}

	@Override
	public final void hide() {
		setVisible(false);
		update();
	}

	protected final IFContext getRootAsContext() {
		if (getRoot() instanceof AbstractComponent)
			return ((AbstractComponent) getRoot()).getRootAsContext();

		if (getRoot() instanceof RootView)
			throw new IllegalStateException("Root is not a context but a regular view");

		return (IFContext) getRoot();
 	}
}
