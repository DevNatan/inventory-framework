# inventory-framework

[![Build](https://github.com/DevNatan/inventory-framework/actions/workflows/build.yml/badge.svg)](https://github.com/DevNatan/inventory-framework/actions/workflows/build.yml)
[![CodeQL](https://github.com/DevNatan/inventory-framework/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/DevNatan/inventory-framework/actions/workflows/codeql-analysis.yml)
[![Jitpack](https://jitpack.io/v/DevNatan/inventory-framework.svg)](https://jitpack.io/#DevNatan/inventory-framework)

Bukkit inventory framework used in some of my projects, feel free to use it. Learn by yourself.

* [Setup](#setup)
* [Preventing Library Conflicts](#preventing-library-conflicts)
* [Getting Started](#getting-started)
    * [Options](#options)
    * [Interaction handling](#interaction-handling)
    * [Managing Data Between Contexts](#managing-data-between-contexts)
    * [Error Handling](#error-handling)
    * [Pagination](#pagination)
      * [Type Declaration](#type-declaration)
      * [Item Rendering](#item-rendering)
      * [Defining the Data Source](#defining-the-data-source)
      * [Layout](#layout)
      * [Previous and Next Page Items](#previous-and-next-page-items)
      * [Title Update on Page Switch](#title-update-on-page-switch)
      * [Final Considerations](#final-considerations)
    * [Open and Close](#open-and-close)
    * [Registration](#registration)
    * [Kotlin DSL](#kotlin-dsl)
    * [Feature Preview](#feature-preview)
* [Version Compatibility](#version-compatibility)
* [Examples](#examples)

## Setup

Before get started, make sure you have the [JitPack repository](https://jitpack.io) included in your
build configuration.

**Gradle (build.gradle)**

```groovy
dependencies {
    compileOnly 'com.github.DevNatan:inventory-framework:2.5'
}
```

**Maven (pom.xml)**

```xml
<dependency>
    <groupId>com.github.DevNatan</groupId>
    <artifactId>inventory-framework</artifactId>
    <version>2.5</version>
    <scope>provided</scope>
</dependency>
```

## Preventing Library Conflicts

There is a good chance that the inventory-framework library will be used in different plugins within
your server, these plugins share the same classpath, if a plugin is using a different version of the
IF compared to the others there will be a **version conflict because it has been shaded** inside
that plugin.

To prevent this, we always provide the library in plugin format (.jar) to be placed in your plugins
folder and used by all your plugins.

Add the inventory framework as a dependency of your plugin to be able to access it.

```yaml
depend: [ InventoryFramework ]
```

**You can install the latest version of the InventoryFramework on
the [Releases tab](https://github.com/DevNatan/inventory-framework/releases) on Github.**

## Getting Started

To start using IF you need to first understand what a View is.

A View is the representation of an inventory with a number of lines that contain items. There are
several types of View such as paginated views.

First, to create a View create a class and extend the View class, override the View's constructor
and fill in the following parameters:

* **rows**: The number of lines your inventory will have.
* **title**: The inventory title.

In your class's constructor you don't have access to any objects so you can only define a static
title and line.
**Let's create an inventory with 3 lines and a cool title.**

```java
import me.saiintbrisson.minecraft.View;

public final class CoolView extends View {

    public CoolView() {
        super(3, "Scooter Turtle");
    }

}
```

You may have noticed that it is possible to extend the View class without defining any constructors,
and this is due to the fact that it **is possible to create a View in a contextual way** that will
give you a way to determine what the title and number of lines of your view will have from a
context.

###### What's a View context?

A viewing context that is also the representation of an inventory but only for a specific entity,
the player, that is, any change in context will only apply to the entity that is present in that
context.

To better understand, the **View is a persistent inventory**, the **context is not persistent**, but
has the same functionality as a View.

##### Filling our View

To put items in your view, you can put them in the view constructor using the `slot` method. There
are variations of this method you can use, such as:

* `slot(number)` at a specified position
* `slot(row, column)` in the `row` and the `column` of the inventory.
* `firstSlot()` in the first inventory slot.
* `lastSlot()` in the last inventory slot.

```java
public final class MyView extends View {

    public MyView() {
        super(3, "Scooter Turtle");

        slot(1, ItemStack);
        slot(3, 5, ItemStack);
        firstSlot(ItemStack);
        lastSlot(ItemStack);
    }

}
```

### Options
The Inventory Framework aims to make your life easier when discussing interactions handling,
so there are a number of options you can enable or disable that target specific views.
For example, you can cancel the click on the entire view just by changing the `cancelOnClick` option.

| Property name              | Default Value | Description                                                                                                                                                                                                                           |
|----------------------------|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| cancelOnClick              | `false`       | Enabling will cancel every click in the view by default. You can do individual treatments per slot using contexts (this will be explained later), by disabling this click so only clicks that are not in that slot will be cancelled. |
| cancelOnShiftClick         | `true`        | Cancels when the player tries to shift-click items within the view's inventory.                                                                                                                                                       |                                                                                                                                                      |
| cancelOnPickup             | `true`        | Cancels picking up items while the view's inventory is open.                                                                                                                                                                          |
| cancelOnDrop               | `true`        | Cancels item drops while the view's inventory is open.                                                                                                                                                                                |
| cancelOnClone              | `true`        | Cancels cloning of items using the middle mouse button.                                                                                                                                                                               |
| cancelOnDrag               | `true`        | Cancels dragging items into the view's inventory.                                                                                                                                                                                     |
| closeOnOutsideClick        | `true`        | Closes the view's inventory when the player clicks outside the inventory.                                                                                                                                                             |
| cancelOnMoveOut            | `true`        | Cancels when a player tries to move items from the view's inventory to the player's inventory.                                                                                                                                        |
| cancelOnMoveIn *(v2.4.0+)* | `true`        | Cancels when a player tries to move items from the player's inventory to the view's inventory.                                                                                                                                        |

All these options can be handled by the developer himself through the View handlers.

### Interaction handling

Of course you want to run functions when an item is clicked, you can use the `onClick(ViewContext)`
handler for that.

The example below will display a message when the player clicks on the diamond.

```java
public final class MyView extends View {

    public MyView() {
        super(3, "Scooter Turtle");

        slot(1, new ItemStack(Material.DIAMOND))
                .onClick(context -> context.getPlayer().sendMessage("Clicked on a Diamond."));
    }

}
```

> Notice that you now have a `context` parameter of type `ViewContext`, which contains context information for the player currently accessing the inventory.

### Rendering function

In the constructor, you don't have the player object that is viewing the inventory available if you
want to use it (unless you use the item's rendering functions, which we'll explain later) so we need
to use our view's `onRender` rendering function to get that.

```java
public final class MyView extends View {

    public MyView() {
        super(3, "Scooter Turtle");
    }

    @Override
    public void onRender(ViewContext context) {
        // ...
    }

}
```

Any function present in the constructor can be used through the `ViewContext`, both of which
extend `VirtualView` which provides the methods. The difference is that these functions **will only
apply to the player in that context**.

With that, we can send a message to the player when the inventory is rendered for him.

```java
public final class MyView extends View {

    public MyView() {
        super(3, "Scooter Turtle");
    }

    @Override
    public void onRender(ViewContext context) {
        final Player player = context.getPlayer();
        player.sendMessage("Hi, " + player.getName() + "!");
    }

}
```

## Managing data between contexts

You also open to a specific player with specific data. This data will be available for as long as
the player has an open inventory, or until it is cleared.

```java
final Map<String, Object> data=new HashMap<>();
        data.put("my-data","My value");

//open to the player with specific data.
        viewFrame.open(MyView.class,player,data);
```

And then, we use it in our view:

```java
// get the data from the context which was passed to the open method.
final String data=context.get("my-data");
```

You can also set a data value during the life of the view.

```java
context.set("my-data",value);
```

And clean it anytime you want too.

```java
context.clear("my-data");
```

> **Context data is transitive!** Any handler that contains a context in that specific View will inherit data from other contexts.

## Error Handling

###### v2.4.1+

Views are encapsulated classes that have their entire functioning determined from external events
that are propagated to methods that handle these events within the View itself, making the subject
that created them not have control of what happens within these methods, causing that is impossible,
for example: doing an error handling.

Before version 2.4.1, when errors occurred in Views they simply stopped working, and it was only
possible to detect these errors through logs on the server console.

You can now integrate your error handling mechanisms (like Sentry) to detect these errors and handle
them accordingly.

### Global Error Handler

You can define a global error handler that will work for all Views.

```java
viewFrame.setErrorHandler((ViewContext ctx, Exception error) -> {
        // ...
});
```

Integrate information from the current context to propagate more detailed errors using Log4j's ThreadContext.
```java
private static final Logger LOGGER = LogManager.getLogger(MyViewFrame.class);

viewFrame.setErrorHandler((ViewContext context, Exception error) -> {
    ThreadContext.put("view", context.getView().getClass().getName());
    ThreadContext.put("player", context.getPlayer().getName());
    LOGGER.error("An error ocurred in some view", error);
    ThreadContext.clear();
});
```

### Per-View Error Handler

You can also define error handling that will only work for a specific View.

Assume that you have a View that, if an error occurs in it, you move the player or send a specific
message that something went wrong according to the context of what your View is.

```java
import me.saiintbrisson.minecraft.View;

public final class WhatIsBurdle extends View {

    public WhatIsBurlde() {
        super(...);
        setErrorHandler((context, error) -> {
            context.close();
            context.getPlayer().sendMessage(
                    "Something went wrong, please try again later."
            );
        });
    }

}
```

### Per-Context Error Handler

It is also possible to define error handlers per player context, that is, you can define an error
handler conditionally depending on your context information.

This error handler will work in all contexts belonging to that player in that View.

###### Preventing context errors to be propagated

By default errors in context error handlers are propagated to the View's error handler, you can
prevent this from happening by changing a property of the ViewContext.

```java
viewContext.setPropagateErrors(false);
```

## Pagination
There are times when we have a large database that needs to be displayed in the inventory,
but Minecraft limits us to having a certain size for inventories that is not enough to 
accommodate this data in the form of an item display.

For that, we turn to Paging, paging allows us to sub-divide our data into multiple sectors (pages)
so that they are displayed within the limit of our inventory. IF helps you to do this easily.

### Type Declaration
To do a paginated inventory you first need to declare the type of data you have.
This type will be defined as a type parameter for the PaginatedView extension, come on.

#### Extending PaginatedView
First, instead of `extend View`, `extend PaginatedView`.
```java
public final class MyPaginatedView extends PaginatedView<T> {
    
}
```

The type parameter `<T>` will be the type of your data source, in these examples we will work with 
integers,
but you can use any type as long as there is a source for them.

```java
public final class MyPaginatedView extends PaginatedView<Integer> {
    
    public MyPaginatedView() {
        super(3, "It's cool :)");
    }
    
}
```

### Item Rendering
The PaginatedView class, which you extended earlier, requires you to override the paginated item 
rendering function, which means: this function will be responsible for determining how such data
will be rendered in the inventory.

**You don't have to worry about anything, just determine what you want to appear and IF will do 
the rest.**

```java
public final class MyPaginatedView extends PaginatedView<Integer> {

    public MyPaginatedView() {
        super(3, "It's cool :)");
    }
    
    @Override
    protected void onItemRender(PaginatedViewSlotContext<Integer> render, ViewItem item, Integer value) {
    }
    
}
```

**Notice that you have three parameters in the item rendering function:**
* `context` It is a **variation of ViewContext** for paginated inventories, it contains current 
  page information, page limit and other player context information;
* `item` An empty **mutable item** entry, this will be the item that you will have to modify so 
  that it fits according to what will be displayed on the page for the player.
* `value` Current value being computed by the IF for the page the player is on.

As I said earlier, you don't have to worry about where your data is, the IF will give you everything 
in the item rendering function.

#### Rendering our Data
Now that I've explained a few things to you, let's render our item

**We will make that when the player clicks, it sends a message in which value he is clicking.**
```java
@Override
protected void onItemRender(
        PaginatedViewSlotContext<Integer> render,
        ViewItem item, 
        Integer value
) {
    render.withItem(createItem(value))
        .onClick(click -> click.getPlayer().sendMessage("Clicked on value " + value));
}

// organize your code into multiple functions to make it easier to understand!
private ItemStack createItem(Integer value) {
    final ItemStack stack = new ItemStack(Material.DIAMOND);
    final ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName("Value: " + value);
    stack.setItemMeta(meta);
    return item;
}
```

### Defining the Data Source
Well, if you tested your paginated inventory code, you probably saw that an error occurred,
that's why we didn't define where the IF will get this data to be rendered from,
you now have to specify your data source.

To define paginated data you must be inside a `PaginatedView` and use the `PaginatedViewContext`'s 
data source definition method.

```java
PaginatedContext#setSource(...)
```

Functions that pass IF context by default are not type safe because of backwards compatibility of the code,
so we provide **an extension for you to turn a ViewContext into a PaginatedViewContext** and access 
the data source definition method.

```java
ViewContext#paginated(); // now it's a PaginatedViewContext
```

#### Scopes
The data source can be defined in three scopes:

##### View (static)
Static persistent data source for the entire View, ideal for **data that will not change at any 
point in your code's lifecycle**, For example: immutable data coming from configuration. 
Defined in View constructor
```java
public MyPaginatedView() {
    super(...);
    setSource(...);
}
```

#### View (dynamic)
Dynamic non-persistent data source for the entire View, ideal for **data that does not require a
player to obtain but will change** at some point in its lifecycle. For example: a list of 
server teleport locations. Defined in View `onRender`;
```java
@Override
protected void onRender(ViewContext render) {
    setSource(...);
}
```

#### Per Context
Dynamic non-persistent data source that depend on a player to be defined. For example: list of 
player houses. Defined in View `onRender`
```java
@Override
protected void onRender(ViewContext render) {
    render.paginated().setSource(...);
}
```

### Layout
Of course you won't leave your items scattered on the menu in a disorderly way, we already expected that and for that we created the layout to make everything beautiful in its place.

The Layout is a pattern of characters that you will use to determine where each item will go on your page.

You can define layouts in two scopes:
* **For the entire View**: the same layout will be used in that view forever.
* **Just for a context**: Only a specific context will use a layout pattern which for some reason 
  must be different from the layout defined in the View or other layouts.

> If you define both layouts, for the View and for the context, the layout of the context will take precedence.

#### Default Pattern
First, as I said before, a layout is a pattern of characters that you will use to determine where items will go,
these characters are:
* `O` a slot that will have a paginated item in the inventory;
* `X` an undefined slot in the inventory;
* `<` a slot in which the item "back page" will be positioned (for paginated views);
* `>` a slot in which the item "next page" will be positioned (for paginated views).

> v2.5+ you can define the default layout definitions with your own character grid.

**There are rules for creating a layout that you should pay attention to**
* The width of the pattern must be the same number of columns as the inventory;
* The height of the pattern must be the same number of rows as the inventory;
* If you define an item in the layout and don't define it in the code an error will be thrown.

Define a layout using `setLayout`.
```java
public final class MyPaginatedView extends PaginatedView<Integer> {

    public MyPaginatedView() {
        super(3, "It's cool :)");
        setLayout(
                "XXXXXXXXX",
                "XOOOOOOOX",
                "XXXXXXXXX"
        );
    }

}
```

You can also set it in the View's `onRender` rendering function as I said earlier too.
```java
@Override
protected void onRender(ViewContext render) {
    setLayout(
        "XXXXXXXXX",
        "XOOOOOOOX",
        "XXXXXXXXX"
    );
}
```

Set the layout only for a specific context using `ViewContext#setLayout`.
```java
@Override
protected void onRender(ViewContext render) {
    render.setLayout(
        "XXXXXXXXX",
        "XOOOOOOOX",
        "XXXXXXXXX"
    );
}
```

In our inventory, items will be positioned in the center, with spaces on the side in the second row.

> Note that the number of rows in my layout is the same as the number of rows in my inventory, and the character span of the layout is the number of columns I have in my inventory, 9.

#### User-defined pattern
###### v2.5+
There are cases where we want to define custom items within our layout, but generally this requires a lot of calculations e.g. to define items at the ends of the inventory, below, above, and anywhere, starting from version 2.5, IF has made it easier for you. life allowing proper characters to be defined in the layout.

So here we go, let's say you want to place panes of glass on the edges of your inventory, like you would have done before:
* Would calculate inventory edges to get correct slot positions
* And then, render in these slots, a pane of glass.

You would probably spend a little time and even have a little headache for this, now you can do it with just one line.
**Let's say `P` is a pane of glass.**
```java
public final class MyPaginatedView extends PaginatedView<Integer> {

    public MyPaginatedView() {
        super(3, "P44t33rns :)");
        setLayout(
                "PPPPPPPPP",
                "POOOOOOOP",
                "PPPPPPPPP"
        );
        setLayout('P', () -> item(new ItemStack(Material.STAINED_GLASS_PANE)));
    }

}
```

> Anything that is set to "P" in our layout will be rendered with the value of our factory set in `setLayout(character, factory)`.

The item factory for the layout is of type `Supplier<ViewItem>`
**`item` is a function provided by IF to create ViewItem.**
```java
setLayout('P', () -> item(...));
```

You can define besides the display item, functions for that item so for example: **you can make the inventory close when someone clicks on that glass pane**.
```java
setLayout('P', () -> item(new ItemStack(Material.STAINED_GLASS_PANE)).onClick(ViewContext::close));
```

#### Updating Context Layout while Inventory is open
Yes, you can change the layout while the player's inventory is open, it only works for the current context. 
This allows you to use multiple layouts and switch between them without having to close the player's inventory and open it again with the updated layout.

To do this, use the View's update function.

```java
@Override
protected void onUpdate(ViewContext update) {
    // will be updated to the player every time
    update.setLayout(...);
}
```

When the View or context update method is called, the layout will be updated.

> Be careful, for changing the layout through the update function to work it is necessary that an 
> initial layout is defined as described in the section on creating layout patterns,
> otherwise the inventory will be without any defined layout.

### Previous and Next Page Items
To set the page change menu items, extend the PaginatedView's `getPreviousPageItem` and `getNextPageItem` methods.

```java
public final class MyPaginatedView extends PaginatedView<Integer> {

    // ... constructor here ...

    @Override
    public ViewItem getPreviousPageItem(PaginatedViewContext<Integer> context) {
        return item(new ItemStack(Material.ARROW));
    }

    @Override
    public ViewItem getNextPageItem(PaginatedViewContext<Integer> context) {
        return item(new ItemStack(Material.BLAZE_ROD));
    }

}
```

> The `item(...)` function comes from the VirtualView class which gives you an empty mutable
> item so you can manipulate it however you want.

There are cases where all paginated inventories have the same page toggle item pattern, 
and so that you don't need to set such items in ALL paginated inventories,
we have a function in the ViewFrame that you can use to set toggle items page for all views coming from that ViewFrame.

```java
viewFrame.setDefaultPreviousPageItem((context) -> {
    return context.item(new ItemStack(Material.BLAZE_ROD));
});

viewFrame.setDefaultNextPageItem((context) -> {
    return context.item(new ItemStack(Material.BLAZE_ROD));
});
```

If you don't want to set page toggle items this way or want to change pages using another item,
the PaginatedViewContext has functions that will help you with that.
* `switchToPreviousPage()` return to previous page;
* `switchToNextPage()` advances to the next page;
* `switchToPage(page)` go to a specific page.

### Title Update on Page Switch
There are cases where we want to display the current page where the player is in the inventory title, 
in some inventory frameworks the code closes the player's current inventory and opens a new one whose 
title contains the page the player went to.

But with the IF this wouldn't work because we don't want 
the player's inventory to close, and more than that, we want their context to hold anyway.

Let's go to the examples, **consider that the title of your inventory is:**
```
My Awesome Inventory
```

But that when the player switches pages **he becomes something similar to this**
```
My Awesome Inventory (4/12)
```

You can use three IF functionalities together to achieve this result, namely:
* `onPageSwitch` is a PaginatedView function that is called when a player switches pages.
* `updateTitle` is a ViewContext function used to change the title of the inventory.
* `resetTitle` is a ViewContext function used to change the title of the inventory to its initial state (after `updateTitle` has been used).

```java
@Override
protected void onPageSwitch(PaginatedViewContext<Integer> pageSwitch) {
    // pages starts at 0
    final int currentPage = pageSwitch.getPage();
   
    // if the player is on the homepage we don't want to display (1/12) 
    // but actually the default title of the inventory so we reset it
    if (currentPage == 0) {
        pageSwitch.resetTitle();
        return;
    }

    // takes the current title and adds the page value to it.
    final int maxPages = pageSwitch.getPagesCount();
    pageSwitch.updateTitle(String.format(
        "%s (%d/%d)",
        pageSwitch.getView().getTitle(), 
        currentPage + 1,
        maxPages
    ));
}
```

When the page is changed, the title will change.

### Final Considerations
Paginated views are a great way to define data in your inventory when you have a large amount of data.

Once you set the item rendering function and data source, you can open the player's inventory and it will be working.

##### Beware of item rendering function!!
The paginated inventory item rendering function is called thousands of times according to your data,
so it is not designed for you to do external computations. The correct thing is that you use 
[Context Data](#managing-data-between-contexts) to determine what will be rendered and to get 
data coming from other contexts.

For example: suppose you have a user object that you need it in the item render function:

**❌ DON'T**\
Don't compute anything inside the item render function.
```java
@Override
protected void onItemRender(
    PaginatedViewSlotContext<Integer> render,
    ViewItem item,
    T value
) {
    User user = getUserFromDatabase()
}
```

**✔️ DO**\
Move the necessary computation to `onOpen`.
```java
private static final String USER_CONTEXT_KEY = "user";

@Override
protected void onOpen(OpenViewContext context) {
    context.set(USER_CONTEXT_KEY, ...);
}

@Override
protected void onItemRender(
    PaginatedViewSlotContext<Integer> render,
    ViewItem item,
    T value
) {
    // now user is available because you defined it on `onOpen`
    User user = render.get(USER_CONTEXT_KEY);
}
```

Or even open the View with the player information.

We do not recommend this method because it is ideal that the methods of the View are encapsulated in it,
and the **context will be defined regardless of the data of who opened the view** (where `open` was 
called), so if you have a view which can be opened in several places in the code, 
for example: clicking on an item in the inventory or executing the command, both will work homogeneously.

It can be used without any problems.
```java
static final String USER_CONTEXT_KEY = "user";

// somewhere you will open the inventory
viewFrame.open(player, ImmutableMap.of(
   USER_CONTEXT_KEY, computedUser
));

@Override
protected void onItemRender(
    PaginatedViewSlotContext<Integer> render,
    ViewItem item,
    T value
) {
    // now user is available because you defined it before
    User user = render.get(USER_CONTEXT_KEY);
}
```

## Open and Close

To open a inventory view you can use `ViewFrame.open`.

```java
viewFrame.open(player, YourView.class);
```

If you have a View instance in hand you can use the `open` without the View class.

```java
view.open(player);
```

You can pass data that will be transitive in the player's context throughout the context's lifecycle
or until removed.

```java
view.open(player,new HashMap<String,Object>(){{
        put("key","value");
}});
```

While in one context, there is a shortener method that takes the player itself as a parameter to the opening method, 
so you don't need to pass anything other than the target view you want to open.
```java
viewContext.open(view);
viewContext.open(view, data);
```

If you want to reuse the data from the current context in other views, for example: a user that was defined only once 
to save resources, use the `transitiveData` parameter of the open function to transfer the data from your current context 
to the next, which will open.
```java
viewContext.open(view, true);

// You can still use custom data, which will be merged with the current data.
viewContext.open(view, data, true);
```

To close an inventory view you can use `View.close`, but pay close attention... This will close the
inventory for all players who have been viewing that inventory.

```java
view.close();
```

You can only close the inventory given to a player if you have his context in hand.

```java
context.close();
```

## Registration

After making our view, we need to register it.

```java
public final class MyPlugin extends JavaPlugin {

    private ViewFrame viewFrame;

    @Override
    public void onEnable() {
        // first, initialize our facade class to store our views
        viewFrame = new ViewFrame(this);

        // add our view to the facade
        viewFrame.addView(MyView.class);

        // then register our facade
        viewFrame.register();
    }

}
```

> The `register` function can only be called once! If you want to dynamically register views use `addView` instead.

To open our view, we need to call the facade method.

```java
// opens "MyView" to the player.
viewFrame.open(MyView.class,player);
```

## Kotlin DSL
###### v2.5.1+
There is a module called [`kotlin-dsl`](https://github.com/DevNatan/inventory-framework/tree/main/kotlin-dsl) that provides extensions that make it easy to create views in Kotlin code. 
You can import it into your project if you are using Kotlin in your project.

Note that IF doesn't include Kotlin's stdlib for you, at runtime you need to have the Kotlin library available in your classpath.

## Feature Preview

###### v2.4.0+

With each update we can launch a prototype of a feature so that whoever uses the IF can test and
give feedback on it, it is possible to enable feature previews using the View feature preview
system.

By default, no prototype or feature preview is enabled, you must explicitly enable it.

```java
View.enableFeaturePreview(ViewFeature.MOVE_IN)
```

> If you have previously enabled a feature preview and it has been released and you happen to have
> forgotten to remove that section of code, an alert will be sent to the logger for you to remove it.

## Version Compatibility

InventoryFramework was initially developed only for version 1.8.8, but over time support for newer
versions has been added.

Currently, inventory-framework should support any Minecraft versions due to its compatibility with
Bukkit. If there's any issue with a different minecraft version, please report it
in [Issue Reporting](https://github.com/DevNatan/inventory-framework/issues) section.

Here is the compatibility table, see if your version is compatible before trying to use the library.

| Minecraft version | Supported since | Status        | Notes |
|-------------------|-----------------|---------------|-------|
| 1.8               | v1.0            | ✅ Supported   |       |
| 1.9–1.15          |                 | ⚠️ Not tested |       |
| 1.16              | v2.3            | ✅ Supported   |       |
| 1.17              | v2.3.2          | ✅ Supported   |       |
| 1.18              | v2.3.2          | ✅ Supported   |       |

## Examples

#### View using rendering function and click handler

```java
public class MyView extends View {

    public MyView() {
        super(3, "My view"); // 3 views per page with title "My view"
    }

    @Override
    public final void onRender(final ViewContext context) {
        // We can get the player from inventory
        Player player = context.getPlayer();

        // We put the Stone in slot 14
        context.slot(14, new ItemStack(Material.STONE));

        // Also, we can put the Stone in row 1, column 5
        context.slot(1, 5, new ItemStack(Material.STONE));

        // We can also put the Stone in row 3, column 5
        ViewItem myItem = context.slot(3, 5, new ItemStack(Material.STONE));

        // We can add a click event
        myItem.onClick(() -> {
            player.sendMessage("You clicked on the stone!");
        });
    }
}
```

There's a lot of things you can do with inventory-framework, but for now, we'll just use the simple
one. You can make paginated pages, refreshing items and schedule tasks inside each view.

## License

inventory-framework is distributed under
the [MIT license](https://github.com/DevNatan/inventory-framework/blob/main/LICENSE).
