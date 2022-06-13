package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractView extends AbstractVirtualView {

	@Getter(AccessLevel.NONE) static final PipelinePhase OPEN = new PipelinePhase("open");
	@Getter(AccessLevel.NONE) static final PipelinePhase RENDER = new PipelinePhase("render");
	@Getter(AccessLevel.NONE) static final PipelinePhase UPDATE = new PipelinePhase("update");
	@Getter(AccessLevel.NONE) static final PipelinePhase CLICK = new PipelinePhase("click");
	@Getter(AccessLevel.NONE) static final PipelinePhase CLOSE = new PipelinePhase("close");

	static final ViewType DEFAULT_TYPE = ViewType.CHEST;

	@ToString.Include private final int rows;
	@ToString.Include private final String title;
	@ToString.Include private final @NotNull ViewType type;

	@Getter(AccessLevel.PROTECTED)
	PlatformViewFrame<?, ?, ?> viewFrame;

	private final Set<ViewContext> contexts = Collections.newSetFromMap(
		Collections.synchronizedMap(new HashMap<>())
	);

	private final Pipeline<ViewContext> pipeline = new Pipeline<>(OPEN, RENDER, UPDATE, CLICK, CLOSE);

	@ToString.Include
	private boolean cancelOnClick, cancelOnPickup, cancelOnDrop, cancelOnDrag, cancelOnClone, cancelOnMoveIn, cancelOnMoveOut, cancelOnShiftClick, clearCursorOnClose, closeOnOutsideClick;

	AbstractView(int rows, String title, @NotNull ViewType type) {
		this.rows = rows;
		this.title = title;
		this.type = type;
		setItems(new ViewItem[type.normalize(rows)]);
	}

	final void open(@NotNull Viewer viewer, @NotNull Map<String, Object> data) {
		final OpenViewContext open = open0(viewer, data);

		// rows will be normalized to fixed container size on `createContainer`
		final int containerSize = open.getContainerSize() == 0 ? rows : open.getContainerSize();

		final String containerTitle = open.getContainerTitle() == null ? title : open.getContainerTitle();
		final ViewType containerType = open.getContainerType() == null ? type : open.getContainerType();

		final ViewContainer container = viewFrame.getFactory().createContainer(
			this,
			containerSize,
			containerTitle,
			containerType
		);

		if (open.isCancelled())
			return;

		final BaseViewContext context = viewFrame.getFactory().createContext(this, container, null);
		context.setItems(new ViewItem[containerType.normalize(containerSize)]);
		context.addViewer(viewer);
		contexts.add(context);
		render(context);
		context.getViewers().forEach(context.getContainer()::open);
	}

	private OpenViewContext open0(@NotNull Viewer viewer, @NotNull Map<String, Object> data) {
		final OpenViewContext context = (OpenViewContext) getViewFrame().getFactory().createContext(
			this,
			null,
			OpenViewContext.class
		);

		context.addViewer(viewer);
		data.forEach(context::set);
		runCatching(context, () -> onOpen(context));
		return context;
	}

	@Override
	protected final void render(@NotNull ViewContext context) {
		getPipeline().execute(RENDER, context);
		super.render(context);
	}

	@Override
	final void update(@NotNull ViewContext context) {
		getPipeline().execute(UPDATE, context);
		super.update(context);
	}

	@Override
	public final void close() {
		// global closings must be always immediate
		closeUninterruptedly();
	}

	@Override
	public final void closeUninterruptedly() {
		getContexts().forEach(ViewContext::close);
	}

	public final Set<ViewContext> getContexts() {
		return Collections.unmodifiableSet(contexts);
	}

	public final ViewContext getContext(@NotNull Predicate<ViewContext> predicate) {
		return contexts.stream().filter(predicate).findFirst().orElse(null);
	}

	@SuppressWarnings("ConstantConditions")
	final ViewItem item() {
		return item(null);
	}

	final ViewItem item(@SuppressWarnings("NullableProblems") @NotNull Object stack) {
		final Object transformedItem = PlatformUtils.getFactory().createItem(stack);
		ViewItem item = new ViewItem();
		item.setItem(transformedItem);
		return item;
	}

	public final boolean isCancelOnClick() {
		return cancelOnClick;
	}

	public final boolean isCancelOnClone() {
		return cancelOnClone;
	}

	public final boolean isCancelOnPickup() {
		return cancelOnPickup;
	}

	public final boolean isCancelOnDrop() {
		return cancelOnDrop;
	}

	public final boolean isCancelOnDrag() {
		return cancelOnDrag;
	}

	public final boolean isCancelOnMoveIn() {
		return cancelOnMoveIn;
	}

	public final boolean isCancelOnMoveOut() {
		return cancelOnMoveOut;
	}

	public final boolean isCancelOnShiftClick() {
		return cancelOnShiftClick;
	}

	public final boolean isClearCursorOnClose() {
		return clearCursorOnClose;
	}

	public final boolean isCloseOnOutsideClick() {
		return closeOnOutsideClick;
	}

	final void setViewFrame(@NotNull PlatformViewFrame<?, ?, ?> viewFrame) {
		this.viewFrame = viewFrame;
	}

	public final void setCancelOnClick(final boolean cancelOnClick) {
		this.cancelOnClick = cancelOnClick;
	}

	public final void setCancelOnPickup(final boolean cancelOnPickup) {
		this.cancelOnPickup = cancelOnPickup;
	}

	public final void setCancelOnDrop(final boolean cancelOnDrop) {
		this.cancelOnDrop = cancelOnDrop;
	}

	public final void setCancelOnDrag(final boolean cancelOnDrag) {
		this.cancelOnDrag = cancelOnDrag;
	}

	public final void setCancelOnClone(final boolean cancelOnClone) {
		this.cancelOnClone = cancelOnClone;
	}

	public final void setCancelOnMoveIn(final boolean cancelOnMoveIn) {
		this.cancelOnMoveIn = cancelOnMoveIn;
	}

	public final void setCancelOnMoveOut(final boolean cancelOnMoveOut) {
		this.cancelOnMoveOut = cancelOnMoveOut;
	}

	public final void setCancelOnShiftClick(final boolean cancelOnShiftClick) {
		this.cancelOnShiftClick = cancelOnShiftClick;
	}

	public final void setClearCursorOnClose(final boolean clearCursorOnClose) {
		this.clearCursorOnClose = clearCursorOnClose;
	}

	public final void setCloseOnOutsideClick(final boolean closeOnOutsideClick) {
		this.closeOnOutsideClick = closeOnOutsideClick;
	}

	@Override
	protected final ViewItem[] getItems() {
		return super.getItems();
	}

	public final Pipeline<ViewContext> getPipeline() {
		return pipeline;
	}

	public final PlatformViewFrame<?, ?, ?> getViewFrame() {
		return viewFrame;
	}

	public final ViewType getType() {
		return type;
	}

	public final int getRows() {
		return rows;
	}

	public final String getTitle() {
		return title;
	}

	@Override
	final void inventoryModificationTriggered() {
		super.inventoryModificationTriggered();
	}

	@Override
	final ViewItem resolve(int index) {
		return super.resolve(index);
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

	protected void onClickOutside(@NotNull ViewContext context) {
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

	@ApiStatus.Experimental
	protected void onMoveIn(@NotNull ViewSlotMoveContext context) {
	}

}