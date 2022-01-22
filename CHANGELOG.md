# Changelog

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