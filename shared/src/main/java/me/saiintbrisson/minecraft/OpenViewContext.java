package me.saiintbrisson.minecraft;

import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.devnatan.inventoryframework.config.ViewConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This context is created before the container is opened, it is used for cancellation by previously
 * defined data, for any reason, and can be used to change the title and size of the container
 * before the rendering intent.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class OpenViewContext extends BaseViewContext implements ViewConfig {

    private String containerTitle;
    private int containerSize;
    private ViewType containerType;
    private boolean cancelled;

    @Getter
    @Setter(AccessLevel.NONE)
    private CompletableFuture<Void> asyncOpenJob;

    OpenViewContext(@NotNull final AbstractView view) {
        super(view, null);
        containerType = view.getType();
    }

    /**
     * Defines the title of the inventory for this context.
     *
     * @param inventoryTitle The new title of the inventory that'll be created.
     * @deprecated Use {@link #title(String)} instead.
     */
    @Deprecated
    public final void setInventoryTitle(@Nullable final String inventoryTitle) {
        title(inventoryTitle);
    }

    /**
     * Defines the size of the inventory for this context, can be the total number of slots or the
     * number of horizontal lines in the inventory.
     *
     * @param inventorySize The new inventory size.
     * @deprecated Use {@link #size(int)} instead.
     */
    @Deprecated
    public final void setInventorySize(final int inventorySize) {
        size(inventorySize);
    }

    /**
     * Waits until the specified job is complete to open the view.
     *
     * @param job The job that'll be waited for.
     */
    public final void waitUntil(@NotNull CompletableFuture<Void> job) {
        this.asyncOpenJob = job;
    }

    @Override
    public void inventoryModificationTriggered() {
        throw new IllegalStateException("It is not allowed to modify the inventory "
                + "in the opening context as the inventory was not even created. "
                + "Use the onRender() rendering function for this.");
    }
}
