package me.devnatan.inventoryframework.context;

import java.util.function.Consumer;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.ViewType;
import org.jetbrains.annotations.NotNull;

public interface IFRenderContext<TItem> extends IFContext {

    @NotNull
    TItem layoutSlot(String character);

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return A {@link TItem item builder} to configure the item.
     */
    @NotNull
    TItem slot(int slot);

    /**
     * Adds an item at the specific row and column in that context's container.
     *
     * @param row    The row in which the item will be positioned.
     * @param column The column in which the item will be positioned.
     * @return A {@link TItem item builder} to configure the item.
     */
    @NotNull
    TItem slot(int row, int column);

    /**
     * Adds an item to the first slot of this context's container.
     *
     * @return A {@link TItem item builder} to configure the item.
     */
    @NotNull
    TItem firstSlot();

    /**
     * Adds an item to the first slot of this context's container.
     *
     * @return A {@link TItem item builder} to configure the item.
     */
    @NotNull
    TItem lastSlot();

    /**
     * Registers a new slot in the next available slot of this view.
     * <p>
     * On layered regular views (layout + AbstractView), layouts that have {@link PaginatedVirtualView#setLayout(char, Consumer) user-defined characters}
     * or that are <a href="https://github.com/DevNatan/inventory-framework/wiki/Pagination#layout-reserved-navigation-characters">non-item reserved characters</a>
     * will be skipped.
     * <pre><code>
     * class MyView extends View {
     *
     *     MyView() {
     *         setLayout("XZZZOOOOX");
     *         setLayout('Z', item -&#60; ...)
     *
     *         // will go to the layout's first available fillable ('O') slot after 'Z'
     *         availableSlot(...);
     *
     *         // not available since layered views slots are defined on layout resolution
     *         int slot = availableSlot(...).getSlot();
     *     }
     * }
     * </code></pre>
     * <p>
     * If the root view has a layout and the context does not have a layout but this function is
     * used, the available slot will be the first slot after the slots rendered from the root view
     * preserving the insertion order, in which case the root view has priority.
     * <pre><code>
     * class MyView extends View {
     *
     *     MyView() {
     *         setLayout("XOOOOOOOOX");
     *         availableSlot(...); // 1
     *     }
     *
     *     &#64;Override
     *     protected void onRender(ViewContext context) {
     *         context.availableSlot(...); // 2
     *     }
     *
     *     &#64;Override
     *     protected void onUpdate(ViewContext context) {
     *         context.availableSlot(...); // DON'T!! Ignored on update context
     *     }
     *
     *     &#64;Override
     *     protected void onClick(ViewSlotClickContext context) {
     *         context.availableSlot(...); // DON'T!! Prohibited on slot context
     *     }
     *
     * }
     * </code></pre>
     *
     * <p>
     * In regular views whose item is not dynamically defined, the slot the item will belong to can
     * be obtained from the returned item instance. Dynamic items will have their starting slot set
     * to {@link IFItem#AVAILABLE}. Use item render function to get around this and get
     * always the correct slot that item is placed on.
     * <pre><code>
     * availableSlot(...).onRender(render -&#60; {
     *     int slot = render.getSlot();
     * });
     * </code></pre>
     * <p>
     * The definition of new slots using this method in a non-dynamic way, that is, defining these
     * slots in the constructor in non-regular views will be treated as regular slots respecting the
     * rules of a regular view and ignoring the contractions of these views if their nature
     * is mostly dynamic.
     * <p>
     * <b>Not supported on paginated views.</b>
     * <ul>
     *     <li>Items of similar types that can possibly be stacked will not be stacked.</li>
     *     <li>
     *         Result slots depending on the {@link AbstractView#getType() view type} will be skipped
     *         if they are not {@link ViewType#canPlayerInteractOn(int) interactable}.
     *     </li>
     * </ul>
     *
     * @return The newly created item instance.
     */
    @NotNull
    TItem availableSlot();
}
