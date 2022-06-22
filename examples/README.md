# Examples

* [Basic Views](#basic-views)
* [Interaction Handling](#interaction-handling)
* Context Manipulation
* [Automatic Updates](#automatic-updates)
* [Conditional Rendering](#conditional-rendering)
* [Paginated Views](#paginated-views)
* [Other Examples](#other-examples)

## Basic Views
* [EmptyView](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/EmptyView.java) An view with no items.
* [DynamicEmptyView](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/DynamicEmptyView.java) An view with no items with container title and size that can be defined from context data
* [RenderingFunction](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/RenderingFunction.java) Sends a message on view render.
* [StaticFilledView](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/RenderingFunction.java) A basic view with items on it.

## Interaction Handling
* [SlotClickHandling](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/SlotClickHandling.java) Performs an action when the player clicks on an item.
* [GlobalClickHandling](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/GlobalClickHandling.java) Sends a message when the player clicks on any item.
* [HoldAndRelease](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/HoldAndRelease.java) Exemplifies the feature of holding and releasing items.
* [AdvancedHoldAndRelease](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/AdvancedHoldAndRelease.java) Advanced hold and release feature example.
* [PreventingItemMoveOut](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/PreventingItemMoveOut.java) Prevents players from moving items out of the view's container.

## Automatic Updates
* [ScheduledUpdate](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/ScheduledUpdate.java) Updates the view every five ticks.
* [ItemUpdateOnAutomaticUpdate](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/ItemUpdateOnAutomaticUpdate.java) Updates an item every five ticks.

## Conditional Rendering
* [StaticViewConditionalRendering](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/StaticViewConditionalRendering.java)
  When the player clicks on the paper the item is hidden or displayed.
* [ContextAwareConditionalRendering](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/ContextAwareConditionalRendering.java)
  Renders a paper if the name of the player viewing the View is "JohnDoe".
  When the player clicks on the paper the item is hidden or displayed.
 
## Paginated Views
* [PersistentPaginatedView](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/PersistentPaginatedView.java) Basic paginated view example using persistent data.
* [PersistentNavigablePaginatedView](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/PersistentNavigablePaginatedView.java) Basic paginated view example using persistent data and navigation items.
* [PersistentLayeredPaginatedView](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/PersistentLayeredPaginatedView.java) Basic paginated view example using persistent data and layout.
* [PersistentLayeredNavigablePaginatedView](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/PersistentLayeredNavigablePaginatedView.java) Basic paginated view example using persistent data, navigation items and layout.
* [PaginatedViewEmptyState](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/PaginatedViewEmptyState.java) Displays an item using conditional rendering if there is no item to be paginated.
* [PaginatedViewBasedOnPlayerInventory](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/PaginatedViewBasedOnPlayerInventory.java) A paginated view whose data are the items in the player's inventory that is opening it.
* [LayeredNavigablePaginatedViewBasedOnPlayerInventory](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/LayeredNavigablePaginatedViewBasedOnPlayerInventory.java) A paginated view whose data are the items in the player's inventory that is opening it with navigation itens and layout.

## Other Examples
* [AmountSelector](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/AmountSelector.java) Amount selector with two buttons to increase/decrease the amount and an item displaying the current amount.
* [RealtimeTitleUpdate](https://github.com/DevNatan/inventory-framework/blob/main/examples/src/main/java/me/saiintbrisson/minecraft/examples/RealtimeTitleUpdate.java) Updates the title of the container or resets it to the initial one when the player interacts with the items in the container without the inventory needing to be closed and reopened.