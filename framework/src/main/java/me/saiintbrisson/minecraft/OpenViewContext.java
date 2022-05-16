package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This context is created before the container is opened, it is used for cancellation by previously
 * defined data, for any reason and can be used to change the title and size of the container before
 * the rendering intent.
 */
@Getter
@ToString(callSuper = true)
public class OpenViewContext extends BaseViewContext implements CancellableViewContext {

	/**
	 * The title of the container that player will see.
	 */
	private String containerTitle;

	/**
	 * The size of the container that player will see.
	 */
	private int containerSize;

	/**
	 * The type of the inventory that player will see.
	 */
	@Setter
	private ViewType viewType;

	@Setter
	private boolean cancelled;

	OpenViewContext(@NotNull final AbstractView view) {
		super(view, null);
	}

	/**
	 * Defines the title of the inventory for this context.
	 *
	 * @param inventoryTitle The new title of the inventory that'll be created.
	 * @deprecated Use {@link #setContainerTitle(String)} instead.
	 */
	@Deprecated
	public void setInventoryTitle(@Nullable final String inventoryTitle) {
		this.containerTitle = inventoryTitle;
	}

	/**
	 * Defines the size of the inventory for this context, can be the total number of slots or the
	 * number of horizontal lines in the inventory.
	 *
	 * @param inventorySize The new inventory size.
	 * @deprecated Use {@link #setContainerSize(int)} instead.
	 */
	@Deprecated
	public void setInventorySize(final int inventorySize) {
		setContainerSize(inventorySize);
	}

	/**
	 * Defines the title of the container for this context.
	 *
	 * @param containerTitle The new title of the container that'll be created.
	 */
	public void setContainerTitle(@Nullable final String containerTitle) {
		this.containerTitle = containerTitle;
	}

	/**
	 * Defines the size of the container for this context, can be the total number of slots or the
	 * number of horizontal lines in the container.
	 *
	 * @param containerSize The new container size.
	 */
	public void setContainerSize(final int containerSize) {
		this.containerSize = getContainer().normalizeSize(containerSize);
	}

	@Override
	void inventoryModificationTriggered() {
		throw new IllegalStateException(
			"It is not allowed to modify the inventory " +
			"in the opening context as the inventory was not even created. " +
			"Use the onRender() rendering function for this."
		);
	}

}
