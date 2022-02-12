# Changelog

# 2.4.0 (unreleased)
* Detect the movement of an item from the player's inventory to their View using `onMoveIn`.
* Some public methods from VirtualView and View become internal, and fast path methods (to ItemStack) from VirtualView becomes `@Deprecated` and will be removed soon.
* Allow PaginatedView layout removal by specifying null on `setLayout`.
* Moved `ReflectionUtils` to a different package to prevent conflicts with other libraries/plugins (thanks to @GeorgeV220 (#31).
* Slot conversion to near-limited row was fixed (#32).
* Introduced a more intuitive way to schedule view updates with`scheduleUpdate`, `ScheduledView` is now deprecated and will be removed soon.
* Now an exception is thrown when trying to open a View without the ViewFrame being registered.
* Now is allowed to set items in the `OpenViewContext` they will be static items but only visible for that context.
* Added `setCloseOnClickOutside` option to close View if player clicks outside the inventory screen.
* `CloseViewContext` now delegates the original context (thanks to @zAlyson).
* View `onMoveIn` feature preview.

# 2.3.2
* Dynamic title update (`ViewContext#updateTitle`) support on 1.17 and 1.18 ([#25](https://github.com/DevNatan/inventory-framework/pull/25)).
* Expose `VirtualView.toSlot` to convert slot numbers.
* Some contexts came with null click origin ([#27](https://github.com/DevNatan/inventory-framework/issues/27)).
* Now an exception is thrown when a View is registered multiple times.
* `onItemRelease` now works with PaginatedView ([#26](https://github.com/DevNatan/inventory-framework/issues/26)).
* Some methods of the ViewFrame class have been marked ***@Deprecated*** as they will be removed in the near future and should not be used.

# 2.3.1
* Assign deprecated annotation in View#getRows for dynamic views([#19](https://github.com/DevNatan/inventory-framework/pull/19))
* View auto update and Spigot 1.16.5 API support ([#18](https://github.com/DevNatan/inventory-framework/pull/18))