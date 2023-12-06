package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.UUID;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.component.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class ComponentContext extends PlatformConfinedContext implements IFComponentRenderContext, Context {

    private final RenderContext parent;
    private final Component component;

    public ComponentContext(RenderContext parent, Component component) {
        this.parent = parent;
        this.component = component;
    }

	@Override
	public IFContext getTopLevelContext() {
		return getParent();
	}

	public RenderContext getParent() {
        return parent;
    }

    @Override
    public final Component getComponent() {
        return component;
    }

    @Override
    public final @NotNull UUID getId() {
        return getParent().getId();
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return getParent().getConfig();
    }

    @Override
    public final Object getInitialData() {
        return getParent().getInitialData();
    }

    @Override
    public final void setInitialData(Object initialData) {
        getParent().setInitialData(initialData);
    }

    @Override
    public final List<Player> getAllPlayers() {
        return getParent().getAllPlayers();
    }

    @Override
    public final void updateTitleForPlayer(@NotNull String title, @NotNull Player player) {
        getParent().updateTitleForPlayer(title, player);
    }

    @Override
    public final void resetTitleForPlayer(@NotNull Player player) {
        getParent().resetTitleForPlayer(player);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final @NotNull PlatformView getRoot() {
        return getParent().getRoot();
    }

    @Override
    public String toString() {
        return "ComponentContext{" + "parent=" + parent + ", component=" + component + "} " + super.toString();
    }
}
