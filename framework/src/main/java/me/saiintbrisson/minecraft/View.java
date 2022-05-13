package me.saiintbrisson.minecraft;

import lombok.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Getter
@RequiredArgsConstructor
public class View extends AbstractVirtualView implements ViewerHolder<Player>, InventoryHolder {

	private final int rows;
	private final String title;

	private final Set<ViewContext> contexts = Collections.newSetFromMap(new WeakHashMap<>());

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.PACKAGE)
	ViewFrame viewFrame;

	public View() {
		this(0);
	}

	public View(int rows) {
		this(rows, null);
	}

	/**
	 * Called before the inventory is opened to the player.
	 * <p>
	 * This handler is often called "pre-rendering" because it is possible to set the title and size
	 * of the inventory and also cancel the opening of the View without even doing any handling related
	 * to the inventory.
	 * <p>
	 * It is not possible to manipulate the inventory in this handler, if it happens an exception
	 * will be thrown.
	 *
	 * @param context The player view context.
	 */
	protected void onOpen(@NotNull OpenViewContext context) {
	}

	/**
	 * Called when this view is rendered to the player.
	 * <p>
	 * This is where you will define items that will be contained non-persistently in the context.
	 * <p>
	 * Using {@link View#slot(int)} here will cause a leak of items in memory or that  the item that
	 * was previously defined will be overwritten as the slot item definition method is for use in
	 * the constructor only once. Instead, you should use the context item definition function
	 * {@link ViewContext#slot(int)}.
	 * <p>
	 * Handlers call order:
	 * <ul>
	 *     <li>{@link #onOpen(OpenViewContext)}</li>
	 *     <li>this rendering function</li>
	 *     <li>{@link #onUpdate(ViewContext)}</li>
	 *     <li>{@link #onClose(ViewContext)}</li>
	 * </ul>
	 * <p>
	 * This is a rendering function and can modify the view's inventory.
	 *
	 * @param context The player view context.
	 */
	protected void onRender(@NotNull ViewContext context) {
	}

	/**
	 * Called when the view is updated for a player.
	 * <p>
	 * This is a rendering function and can modify the view's inventory.
	 *
	 * @param context The player view context.
	 * @see View#update()
	 * @see ViewContext#update()
	 */
	protected void onUpdate(@NotNull ViewContext context) {
	}

	/**
	 * Called when the player closes the view's inventory.
	 * <p>
	 * It is possible to cancel this event and have the view's inventory open again for the player.
	 *
	 * @param context The player view context.
	 */
	protected void onClose(@NotNull ViewContext context) {
	}

	/**
	 * Called when a player clicks on the view inventory.
	 * <p>
	 * This function is called even if the click has been cancelled, you can check this using
	 * {@link ViewSlotContext#isCancelled()}.
	 * <p>
	 * Canceling the context will cancel the click.
	 * <p>
	 * Handling the inventory in the click handler is not allowed.
	 *
	 * @param context The player view context.
	 */
	protected void onClick(@NotNull ViewSlotContext context) {
	}

	/**
	 * Called when a player uses the hot bar key button.
	 * <p>
	 * This context is non-cancelable.
	 *
	 * @param context      The current view context.
	 * @param hotbarButton The interacted hot bar button.
	 */
	protected void onHotbarInteract(@NotNull ViewContext context, int hotbarButton) {
	}

	/**
	 * Called when the player holds an item in the inventory.
	 * <p>
	 * This handler will only work if the player manages to successfully hold the item, for example
	 * it will not be called if the click has been canceled for whatever reasons.
	 * <p>
	 * This context is non-cancelable.
	 *
	 * @param context The player view context.
	 */
	protected void onItemHold(@NotNull ViewSlotContext context) {
	}

	/**
	 * Called when an item is dropped by the player in an inventory (not necessarily the View's inventory).
	 * <p>
	 * With this it is possible to detect if the player held and released an item:
	 * <ul>
	 *     <li>inside the view</li>
	 *     <li>outside the view (in the player inventory)</li>
	 *     <li>from inside to outside the view (to the player inventory)</li>
	 *     <li>from outside to inside the view (from the player inventory)</li>
	 * </ul>
	 * <p>
	 * This handler is the counterpart of {@link #onItemHold(ViewSlotContext)}.
	 *
	 * @param from The input context of the move.
	 * @param to   The output context of the move.
	 */
	protected void onItemRelease(
		@NotNull ViewSlotContext from,
		@NotNull ViewSlotContext to
	) {
	}

	/**
	 * Called when a player moves a view item out of the view's inventory.
	 * <p>
	 * Canceling the context will cancel the move.
	 * Don't confuse moving with dropping.
	 *
	 * @param context The player view context.
	 */
	protected void onMoveOut(@NotNull ViewSlotMoveContext context) {
	}

	protected void onMoveIn(@NotNull ViewSlotMoveContext context) {
	}

	@Override
	public final void open(@NotNull Viewer viewer, @NotNull Map<String, Object> data) {
		final OpenViewContext openViewContext = open0(viewer, data);
		final ViewContainer container = viewFrame.getFactory().createContainer(
			this,
			openViewContext.getContainerSize(),
			openViewContext.getContainerTitle()
		);

		final ViewContext context = create(viewer, container);
		synchronized (contexts) {
			contexts.add(context);
		}

		render(context);
		context.getContainer().open(context.getViewers());
	}

	@Override
	public final void open(
		@NotNull Player player,
		@NotNull Map<String, Object> data
	) {
		open(viewFrame.getFactory().createViewer(player), data);
	}

	private OpenViewContext open0(@NotNull Viewer viewer, @NotNull Map<String, Object> data) {
		final OpenViewContext context = new OpenViewContext(this);
		context.addViewer(viewer);
		data.forEach(context::set);
		runCatching(context, () -> onOpen(context));
		return context;
	}

	private void render(@NotNull ViewContext context) {
		runCatching(context, () -> onRender(context));
	}

	final ViewContext create(
		@NotNull Viewer viewer,
		@NotNull ViewContainer container
	) {
		final ViewContext context = viewFrame.getFactory().createContext(this, container);
		context.addViewer(viewer);
		return context;
	}

	@Override
	public final void close() {
		getContexts().forEach(ViewContext::close);
	}

	@SuppressWarnings("ConstantConditions")
	@NotNull
	@Override
	public final Inventory getInventory() {
		return null;
	}

}
