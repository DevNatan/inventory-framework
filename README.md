# inventory-framework
[![badge](https://jitpack.io/v/DevNatan/inventory-framework.svg)](https://jitpack.io/#DevNatan/inventory-framework)

Bukkit inventory framework used in some of my projects, feel free to use it. Learn by yourself.

* [Setup](#setup)
* [Preventing Library Conflicts](#preventing-library-conflicts)
* [Getting Started](#getting-started)
  * [Interaction handling](#interaction-handling)
  * [Managing data between contexts](#managing-data-between-contexts)
  * [Open and Close](#open-and-close)
  * [Registration](#registration)
  * [Feature Preview](#feature-preview)
* [Version Compatibility](#version-compatibility)
* [Examples](#examples)

## Setup
Before get started, make sure you have the [JitPack repository](https://jitpack.io) included in your build configuration.

**Gradle (build.gradle)**
```groovy
dependencies {
    compileOnly 'com.github.DevNatan:inventory-framework:2.3.2'
}
```

**Maven (pom.xml)**
```xml
<dependency>
    <groupId>com.github.DevNatan</groupId>
    <artifactId>inventory-framework</artifactId>
    <version>2.3.2</version>
    <scope>provided</scope>
</dependency>
```

## Preventing Library Conflicts
There is a good chance that the inventory-framework library will be used in different plugins within your server, 
these plugins share the same classpath, if a plugin is using a different version of the IF compared to the others 
there will be a **version conflict because it has been shaded** inside that plugin.

To prevent this, we always provide the library in plugin format (.jar) to be placed in your plugins folder and used by all your plugins.

Add the inventory framework as a dependency of your plugin to be able to access it.
```yaml
depend: [InventoryFramework]
```

**You can install the latest version of the InventoryFramework on the [Releases tab](https://github.com/DevNatan/inventory-framework/releases) on Github.**

## Getting Started
To start using IF you need to first understand what a View is.

A View is the representation of an inventory with a number of lines that contain items. 
There are several types of View such as paginated views.

First, to create a View create a class and extend the View class, 
override the View's constructor and fill in the following parameters:

* **rows**: The number of lines your inventory will have.
* **title**: The inventory title.

In your class's constructor you don't have access to any objects so you can only define a static title and line.
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
give
you a way to determine what the title and number of lines of your view will have from a context.

###### What's a View context?
A viewing context that is also the representation of an inventory but only for a specific entity, 
the player, that is, any change in context will only apply to the entity that is present in that context.

To better understand, the **View is a persistent inventory**, the **context is not persistent**, 
but has the same functionality as a View.

##### Filling our View
To put items in your view, you can put them in the view constructor using the `slot` method.
There are variations of this method you can use, such as:
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

### Interaction handling
Of course you want to run functions when an item is clicked, you can use the `onClick(ViewContext)` handler for that.

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
In the constructor, you don't have the player object that is viewing the inventory available if you want to use it (unless you use the item's rendering functions, which we'll explain later) so we need to use our view's `onRender` rendering function to get that.

```java
public final class MyView extends View {
    
    public MyView() {
        super(3, "Scooter Turtle");
    }
    
    @Override
    public final void onRender(final ViewContext context) {
        // ...
    }
    
}
```

Any function present in the constructor can be used through the `ViewContext`, both of which extend `VirtualView` which provides the methods.
The difference is that these functions **will only apply to the player in that context**.

With that, we can send a message to the player when the inventory is rendered for him.
```java
public final class MyView extends View {
    
    public MyView() {
        super(3, "Scooter Turtle");
    }
    
    @Override
    public final void onRender(final ViewContext context) {
        final Player player = context.getPlayer();
        player.sendMessage("Hi, " + player.getName() + "!");
    }
    
}
```

## Managing data between contexts
You also open to a specific player with specific data.
This data will be available for as long as the player has an open inventory, or until it is cleared.
```java
final Map<String, Object> data = new HashMap<>();
data.put("my-data", "My value");

//open to the player with specific data.
viewFrame.open(MyView.class, player, data);
```

And then, we use it in our view:
```java
// get the data from the context which was passed to the open method.
final String data = context.get("my-data");
```

You can also set a data value during the life of the view.
```java
context.set("my-data", value);
```

And clean it anytime you want too.
```java
context.clear("my-data");
```

> **Context data is transitive!** Any handler that contains a context in that specific View will inherit data from other contexts. 

## Open and Close
To open a inventory view you can use `ViewFrame.open`.
```java
viewFrame.open(player, YourView.class);
```

If you have a View instance in hand you can use the `open` without the View class.
```java
view.open(player);
```

You can pass data that will be transitive in the player's context throughout the context's lifecycle or until removed.
```java
view.open(player, new HashMap<String, Object>() {{
    put("key", "value");
}});
```

To close an inventory view you can use `View.close`, but pay close attention... This will close the inventory for all players who have been viewing that inventory.
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
viewFrame.open(MyView.class, player);
```

## Feature Preview
###### v2.4.0+
With each update we can launch a prototype of a feature so that whoever uses the IF can test and give feedback on it,
it is possible to enable feature previews using the View feature preview system.

By default, no prototype or feature preview is enabled, you must explicitly enable it.
```java
View.enableFeaturePreview(ViewFeature.MOVE_IN)
```

> If you have previously enabled a feature preview and it has been released and you happen to have
> forgotten to remove that section of code, an alert will be sent to the logger for you to remove it.

## Version Compatibility
InventoryFramework was initially developed only for version 1.8.8, but over time support for newer versions has been added.

Currently, inventory-framework should support any Minecraft versions due to its compatibility
with Bukkit. If there's any issue with a different minecraft version, please report it in [Issue 
Reporting](https://github.com/DevNatan/inventory-framework/issues) section.

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

There's a lot of things you can do with inventory-framework, but for now, we'll just use the simple one.
You can make paginated pages, refreshing items and schedule tasks inside each view.

## License
inventory-framework is distributed under the [MIT license](https://github.com/DevNatan/inventory-framework/blob/main/LICENSE).
