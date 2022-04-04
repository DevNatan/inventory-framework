# Changelog

# 2.5 (unreleased)
* New `cancelOnMoveIn` option to cancel moving items to the inventory (yet under development can be
  buggy some times).
* New `onHotbarInteract(...)` to handle hotbar button interacts.

# 2.4.2
* Previous and next page items aren't displayed if item slot is manually defined ([#44][i44])
* Add Jetbrains annotations as transitive dependency ([#45][i45])
* Error handler is not working in paginated inventory ([#41][i41])

# 2.4.1
* Error Handling prototype.
* No longer possible to use shift-click to move items from the player's inventory to the view's
  inventory that has `cancelOnClick` or `cancelOnShiftClick` enabled.
* Fixed bug where `closeOnOutsideClick` closed the View for all its contexts not just for the player.
* Fixed clicks being cancelled in player inventory if view is marked to cancel on click even 
  player is not clicking in the view inventory.
* Now `closeOnOutsideClick` View option are enabled by default.
* Build project on JDK 8 (thanks to @luiz-otavio [#40][i40])

# 2.4.0
* ~~Detect the movement of an item from the player's inventory to their View using `onMoveIn` 
  feature preview.~~ (postponed to 2.5)
* Some public methods from VirtualView and View become internal, and fast path methods (to ItemStack)
  from VirtualView becomes `@Deprecated` and will be removed soon.
* Allow PaginatedView layout removal by specifying null on `setLayout`.
* Moved `ReflectionUtils` to a different package to prevent conflicts with other 
  libraries/plugins (thanks to @GeorgeV220 ([#31][i31]).
* Slot conversion to near-limited row was fixed ([#32][i32]).
* Introduced a more intuitive way to schedule view updates with`scheduleUpdate`, `ScheduledView` is
  now deprecated and will be removed soon.
* Now an exception is thrown when trying to open a View without the ViewFrame being registered.
* Now is allowed to set items in the `OpenViewContext` they will be static items but only visible for that context.
* Added `setCloseOnClickOutside` option to close View if player clicks outside the inventory screen.
* `CloseViewContext` now delegates the original context (thanks to @zAlyson).
* An exception will be thrown when an inventory tries to be modified in the `onOpen` View handler.

# 2.3.2
* Dynamic title update (`ViewContext#updateTitle`) support on 1.17 and 1.18 ([#25][p25]).
* Expose `VirtualView.toSlot` to convert slot numbers.
* Some contexts came with null click origin ([#27][p27]).
* Now an exception is thrown when a View is registered multiple times.
* `onItemRelease` now works with PaginatedView ([#26][p26]).
* Some methods of the ViewFrame class have been marked ***@Deprecated*** as they will be removed in the near future and should not be used.

# 2.3.1
* Assign deprecated annotation in View#getRows for dynamic views([#19][p19])
* View auto update and Spigot 1.16.5 API support ([#18][p18])

[p18]: https://github.com/DevNatan/inventory-framework/pull/18
[p19]: https://github.com/DevNatan/inventory-framework/pull/19
[p25]: https://github.com/DevNatan/inventory-framework/pull/25
[p26]: https://github.com/DevNatan/inventory-framework/pull/26
[p27]: https://github.com/DevNatan/inventory-framework/pull/27
[i31]: https://github.com/DevNatan/inventory-framework/issues/31
[i32]: https://github.com/DevNatan/inventory-framework/issues/32
[i40]: https://github.com/DevNatan/inventory-framework/issues/40
[i41]: https://github.com/DevNatan/inventory-framework/issues/41
[i44]: https://github.com/DevNatan/inventory-framework/issues/44
[i45]: https://github.com/DevNatan/inventory-framework/issues/45