package me.saiintbrisson.minecraft;

import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This context is created before the container is opened, it is used for cancellation by previously
 * defined data, for any reason, and can be used to change the title and size of the container
 * before the rendering intent.
 */
@Getter
@ToString(callSuper = true)
public class OpenViewContext extends BaseViewContext {

    /** The title of the container that player will see. */
    private String containerTitle;

    /** The size of the container that player will see. */
    private int containerSize;

    /** The type of the container that player will see. */
    @Setter
    private ViewType containerType;

    @Getter
    private CompletableFuture<?> asyncOpenJob;

    @Setter
    private boolean cancelled;

    OpenViewContext(@NotNull final AbstractView view) {
        super(view, null);
        containerType = view.getType();
    }

    /**
     * Defines the title of the inventory for this context.
     *
     * @param inventoryTitle The new title of the inventory that'll be created.
     * @deprecated Use {@link #setContainerTitle(String)} instead.
     */
    @Deprecated
    public final void setInventoryTitle(@Nullable final String inventoryTitle) {
        setContainerTitle(inventoryTitle);
    }

    /**
     * Defines the size of the inventory for this context, can be the total number of slots or the
     * number of horizontal lines in the inventory.
     *
     * @param inventorySize The new inventory size.
     * @deprecated Use {@link #setContainerSize(int)} instead.
     */
    @Deprecated
    public final void setInventorySize(final int inventorySize) {
        setContainerSize(inventorySize);
    }

    /**
     * Defines the title of the container for this context.
     *
     * @param containerTitle The new title of the container that'll be created.
     */
    public final void setContainerTitle(@Nullable final String containerTitle) {
        this.containerTitle = containerTitle;
    }

    /**
     * Defines the size of the container for this context, can be the total number of slots or the
     * number of horizontal lines in the container.
     *
     * @param containerSize The new container size.
     */
    public final void setContainerSize(final int containerSize) {
        if (getContainerType() == null)
            throw new IllegalStateException(
                    "Cannot find a defined or fallback view type to determine the container size. "
                            + "Set it via #setContainerType or on root view constructor");

        this.containerSize = containerSize;
    }

    /**
     * Waits until the specified job is complete to open the view.
     *
     * @param job The job that'll be waited for.
     */
    public final void waitUntil(@NotNull CompletableFuture<?> job) {
        this.asyncOpenJob = job;
    }

    @Override
    public void inventoryModificationTriggered() {
        throw new IllegalStateException("It is not allowed to modify the inventory "
                + "in the opening context as the inventory was not even created. "
                + "Use the onRender() rendering function for this.");
    }
}
