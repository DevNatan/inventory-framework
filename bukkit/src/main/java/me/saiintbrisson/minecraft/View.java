package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.IFOpenContext;
import me.devnatan.inventoryframework.IFRenderContext;
import me.devnatan.inventoryframework.IFSlotClickContext;
import me.devnatan.inventoryframework.IFSlotContext;
import me.devnatan.inventoryframework.IFSlotMoveContext;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.config.ViewConfig;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform View backward compatible implementation.
 */
@ApiStatus.OverrideOnly
public class View extends AbstractView implements RootView {

	protected View() {
		this(0);
	}

	protected View(int size) {
		this(size, null);
	}

	protected View(String title) {
		this(0, title);
	}

	protected View(String title, @NotNull ViewType type) {
		this(0, title, type);
	}

	protected View(@NotNull ViewType type) {
		this(0, null, type);
	}

	protected View(int size, String title) {
		this(size, title, ViewType.CHEST);
	}

	public View(int size, String title, @NotNull ViewType type) {
		super(size, title, type);
	}

	/**
	 * Called when the view is about to be configured, the returned object will be the view's
	 * configuration.
	 * <p>
	 * As a reference, the data defined here was defined in the constructor in previous versions.
	 *
	 * @return The view configuration.
	 */
	@ApiStatus.OverrideOnly
	protected ViewConfig onInit() {
		return ViewConfig.create();
	}

	/**
	 * Called before the inventory is opened to the player.
	 *
	 * <p>This handler is often called "pre-rendering" because it is possible to set the title and
	 * size of the inventory and also cancel the opening of the View without even doing any handling
	 * related to the inventory.
	 *
	 * <p>It is not possible to manipulate the inventory in this handler, if it happens an exception
	 * will be thrown.
	 *
	 * @param open The player view context.
	 */
	protected void onOpen(OpenViewContext open) {}

	/**
	 * Called __once__ when this view is rendered to the player for the first time.
	 *
	 * <p>This is where you will define items that will be contained non-persistently in the context.
	 *
	 * <p>Using {@link #slot(int)} here will cause a leak of items in memory or that the item
	 * that was previously defined will be overwritten as the slot item definition method is for use
	 * in the constructor only once. Instead, you should use the context item definition function
	 * {@link IFContext#slot(int)}.
	 *
	 * <p>Handlers call order:
	 *
	 * <ul>
	 *   <li>{@link #onOpen(OpenViewContext)}
	 *   <li>this rendering function
	 *   <li>{@link #onUpdate(ViewContext)}
	 *   <li>{@link #onClose(ViewContext)}
	 * </ul>
	 *
	 * <p>This is a rendering function and can modify the view's container, it's called once.
	 *
	 * @param context The player view context.
	 */
	@ApiStatus.OverrideOnly
	protected void onRender(@NotNull ViewContext context) {}

	/**
	 * TODO add this on 2.5.5
	 *
	 * Called only once before the container is displayed to a player.
	 * <p>
	 * The {@code context} is not cancelable, for cancellation use open handler instead.
	 * <p>
	 * This function should only be used to render items, any external call is completely forbidden
	 * as the function runs on the main thread.
	 *
	 * @param context The renderization context.
	 */
//	@ApiStatus.OverrideOnly
//	protected void onRender(ViewRenderContext context) {}

	/**
	 * Called when a slot (even if it's a pseudo slot) is rendered.
	 *
	 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @param context The slot render context.
	 */
	@ApiStatus.Experimental
	protected void onSlotRender(@NotNull ViewSlotContext context) {}

	/**
	 * Called when the view is updated for a player.
	 *
	 * <p>This is a rendering function and can modify the view's inventory.
	 *
	 * @param context The player view context.
	 * @see IFContext#update()
	 */
	protected void onUpdate(@NotNull ViewContext context) {}

	/**
	 * Called when the player closes the view's inventory.
	 *
	 * <p>It is possible to cancel this event and have the view's inventory open again for the player.
	 *
	 * @param context The player view context.
	 */
	protected void onClose(@NotNull ViewContext context) {}

	/**
	 * Called when an actor clicks on a container while it has a view open.
	 *
	 * <p>You can know if the click was on entity inventory or view inventory by {@link
	 * IFSlotContext#isOnEntityContainer()}
	 *
	 * <p>Any function that triggers an {@link #inventoryModificationTriggered() inventory
	 * modification} is prohibited from being used in this handler.
	 *
	 * <p>This context is cancelable and canceling this context will cancel the click, thus canceling
	 * all subsequent interceptors causing the pipeline to terminate immediately.
	 *
	 * @param context The click context.
	 */
	protected void onClick(@NotNull ViewSlotClickContext context) {}

	/**
	 * Called when the player holds an item in the inventory.
	 *
	 * <p>This handler will only work if the player manages to successfully hold the item, for example
	 * it will not be called if the click has been canceled for whatever reasons.
	 *
	 * <p>This context is non-cancelable.
	 *
	 * @param context The player view context.
	 */
	protected void onItemHold(@NotNull ViewSlotContext context) {}

	/**
	 * Called when an item is dropped by the player in an inventory (not necessarily the View's
	 * inventory).
	 *
	 * <p>With this it is possible to detect if the player held and released an item:
	 *
	 * <ul>
	 *   <li>inside the view
	 *   <li>outside the view (in the player inventory)
	 *   <li>from inside to outside the view (to the player inventory)
	 *   <li>from outside to inside the view (from the player inventory)
	 * </ul>
	 *
	 * <p>This handler is the counterpart of {@link #onItemHold(ViewSlotContext)}.
	 *
	 * @param fromContext The input context of the move.
	 * @param toContext   The output context of the move.
	 */
	protected void onItemRelease(@NotNull ViewSlotContext fromContext, @NotNull ViewSlotContext toContext) {}

	/**
	 * Called when a player moves a view item out of the view's inventory.
	 *
	 * <p>Canceling the context will cancel the move. Don't confuse moving with dropping.
	 *
	 * @param context The player view context.
	 */
	protected void onMoveOut(@NotNull ViewSlotMoveContext context) {}

	/**
	 * Called when a context is resumed.
	 * <p>
	 * Currently, there is only one way to resume a context, which is through
	 * {@link IFContext#back()} the "context" parameter being the context that was resumed and
	 * the "subject" the context in which the function was executed.
	 *
	 * @param context The resumed context.
	 * @param subject By what context the context was resumed.
	 */
	protected void onResume(@NotNull ViewContext context, @NotNull ViewContext subject) {}

}
