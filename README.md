# inventory-framework

[![Build](https://github.com/DevNatan/inventory-framework/actions/workflows/build.yml/badge.svg)](https://github.com/DevNatan/inventory-framework/actions/workflows/build.yml)
[![CodeQL](https://github.com/DevNatan/inventory-framework/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/DevNatan/inventory-framework/actions/workflows/codeql-analysis.yml)
[![Jitpack](https://jitpack.io/v/DevNatan/inventory-framework.svg)](https://jitpack.io/#DevNatan/inventory-framework)

**IF** (**I**nventory **F**ramework) is a Bukkit Inventory API framework that allows you to create custom inventories
with varied interaction treatments that would be almost impossible to create manually by the developer.

We provide to you with extra inventory manipulation functionality, common code for creating inventories of different types,
resolution and detection of known platform issues that are resolved internally, high-level secure and robust API.

* [Installation](https://github.com/DevNatan/inventory-framework/wiki/Installation)
* [Documentation](https://github.com/DevNatan/inventory-framework/wiki)
* [Getting Started](#getting-started)
    * [Options](#options)
    * [Interaction handling](#interaction-handling)
    * [Managing Data Between Contexts](#managing-data-between-contexts)
    * [Open and Close](#open-and-close)
    * [Registration](#registration)

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
Documentation has been moved to [Wiki](https://github.com/DevNatan/inventory-framework/wiki/Error-Handling-(v2.4.0-)).

## Pagination
Documentation has been moved to [Wiki](https://github.com/DevNatan/inventory-framework/wiki/Pagination).

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

## License
inventory-framework is distributed under the [MIT license](https://github.com/DevNatan/inventory-framework/blob/main/LICENSE).
