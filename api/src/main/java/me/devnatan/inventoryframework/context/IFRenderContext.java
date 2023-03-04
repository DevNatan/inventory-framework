package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.component.IFItemBuilder;
import org.jetbrains.annotations.NotNull;

public interface IFRenderContext extends IFConfinedContext {

    IFContext getParent();

    /**
     * This allows access the current configuration with the possibility to change it only for that
     * context.
     * <p>
     * By default, all contexts inherit their root configuration, context configuration always takes
     * precedence over root.
     * <p>
     * Options that change the nature of the container are not allowed to be modifier as the
     * container has already been created at that point.
     *
     * @return The current context configuration.
     */
    @NotNull
    ViewConfigBuilder modifyConfig();
}
