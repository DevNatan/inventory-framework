package me.saiintbrisson.minecraft.pipeline.interceptors;

import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Intercepts the rendering phase and renders the layout of a view.
 */
public final class LayoutRenderInterceptor implements PipelineInterceptor<VirtualView> {

	@Override
	public void intercept(
		@NotNull PipelineContext<VirtualView> pipeline,
		VirtualView view
	) {
		// layout cannot be rendered without a context (no container available)
		if (!(view instanceof ViewContext))
			return;

		final ViewContext context = (ViewContext) view;
		final AbstractView root = context.getRoot();
		final String[] layout = view.getLayout();

		final String[] contextLayout = view.getLayout();

		// context layout will be used as fallback to render context-scope defined items
		if (contextLayout != null || root.getLayout() != null) {
			final boolean inheritedFromRoot = contextLayout == null && root.getLayout() != null;
			final boolean contextLayoutResolved = context.isLayoutSignatureChecked();

			if (contextLayout != null && !contextLayoutResolved)
				throw new IllegalStateException("Context layout must be resolved before render");

			// inherits the layout items layer from root if the layout was inherited
			final Stack<Integer> layoutItemsLayer = new Stack<>();
			layoutItemsLayer.addAll(!contextLayoutResolved
				? root.getLayoutItemsLayer()
				: context.getLayoutItemsLayer()
			);

			// drops all items that have been rendered before as these slots have already been filled,
			// the next slots will be used to render the items in context
			int dropCount = root.getReservedItemsCount();
			while (dropCount > 0) {
				layoutItemsLayer.removeElementAt(0);
				dropCount--;
			}

			renderLayout(context, context, layoutItemsLayer, inheritedFromRoot);
		}
	}

	private void renderLayout(
		@NotNull VirtualView view,
		@Nullable ViewContext context,
		Stack<Integer> layoutItemsLayer,
		boolean inheritedFromRoot
	) {
		if (!inheritedFromRoot && !view.isLayoutSignatureChecked())
			throw new IllegalStateException("Layout must be resolved before render");

		final Deque<ViewItem> reservedItems = view.getReservedItems();

		// skip if reserved items defined by auto-slot-filling was already consumed
		if (reservedItems == null || reservedItems.isEmpty()) return;

		final int reservedItemsCount = reservedItems.size();
		for (int i = 0; i < reservedItemsCount; i++) {
			final int targetSlot;
			try {
				targetSlot = layoutItemsLayer.elementAt(i);
			} catch (final ArrayIndexOutOfBoundsException e) {
				throw new RuntimeException("No more slots available on layout.", e);
			}

			final ViewItem next;

			try {
				// remove first to preserve insertion order
				next = reservedItems.removeFirst();
			} catch (final NoSuchElementException ignored) {
				break;
			}

			// register item on view since dynamically rendered items are not registered, so we need
			// to register then on the first time that it's get rendered
			view.apply(next, targetSlot);

			if (context != null)
				context.getRoot().render(context, next, targetSlot);
		}
	}

}
