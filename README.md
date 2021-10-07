I strongly advise against using this project.
---
This lib is not stable nor completed, there aren't any plans to finish it.

# inventory-framework

* [Virtual View](#virtual-view)
* [View](#view)
  * [Creating items](#creating-items)
  * [You need to know](#you-need-to-know)
    * [Straight changes](#straight-changes)
    * [Staticity](#staticity)
* [Player data](#player-data)
* [Menu handlers](#menu-handlers)
  * [Open handler](#open-handler)
  * [Close handler](#close-handler)
* [Hooking Into](#hooking-into)
* [Recommended Use](#recommended-use)

## Virtual View
A [VirtualView](https://github.com/SaiintBrisson/inventory-framework/blob/master/src/main/java/me/saiintbrisson/minecraft/VirtualView.java) represents a set of items in any form of ordering with no specific designation.\
It serves as a basis for creating other views and deals with basic functions for rendering and updating items.

Hidden for the end user, he cannot manipulate it directly, only through handlers.

## View
[View](https://github.com/SaiintBrisson/inventory-framework/blob/master/src/main/java/me/saiintbrisson/minecraft/View.java) is the first [VirtualView](https://github.com/SaiintBrisson/inventory-framework/blob/master/src/main/java/me/saiintbrisson/minecraft/VirtualView.java) implementation containing everything it has in addition to a container with slots (inventory) for defining items, title, sizing, handlers and other features. To use, just create a new class and extend it.
```java
public final class MyView extends View {
        
    public MyView() {
        super(size, title);
    }

}
```

In the `size` parameter of the constructor you can specify the number of lines of the container or its total size, **the total size must be a multiple of 9**.

### Creating items
To create items for your container is very easy, you just need to know what position it should be in.
Available for any [VirtualView](https://github.com/SaiintBrisson/inventory-framework/blob/master/src/main/java/me/saiintbrisson/minecraft/VirtualView.java) implementation.
```java
public final class MyView extends View {
        
    public MyView() {
        super(size, title);
        
        slot(3, new ItemStack(Material.DIAMOND));
    }

}
```

You can also use row-column orientation if you want it to be easier.
```java
// row 6 – column 1
slot(6, 1, new ItemStack(Material.DIAMOND));
```

Or use the [*`firstSlot(item)`*](https://github.com/SaiintBrisson/inventory-framework/blob/94cf20a57c53a874c77e8bd71a668d6573c3e005/src/main/java/me/saiintbrisson/minecraft/VirtualView.java#L115) and [*`lastSlot(item)`*](https://github.com/SaiintBrisson/inventory-framework/blob/94cf20a57c53a874c77e8bd71a668d6573c3e005/src/main/java/me/saiintbrisson/minecraft/VirtualView.java#L134) shortcuts to use positions based on the size of your inventory without needing to know how much it is.
```java
firstSlot(new ItemStack(Material.DIAMOND));
lastSlot(new ItemStack(Material.PAPER));
```

### You need to know
#### Straight changes
From the moment you create a new [View](https://github.com/SaiintBrisson/inventory-framework/blob/master/src/main/java/me/saiintbrisson/minecraft/View.java), it becomes just one. Everyone who has access to it has shared the same status, that is, any changes you make to it will apply to everyone who has it active at that time.

**For example**:\
You have 5 players and they all opened the same View and will leave it open for a certain period of time.
You have the need to change an item in your container, you want to change its quantity from 10 to 8.
```java
public final class MyView extends View {
        
    public MyView() {
        super(size, title);
        
        firstSlot(new ItemStack(Material.DIAMOND, 10));
    }
    
    @Override
    public void onClick(final ViewSlotContext context) {
        // this was a direct modification to the item without updating the contextual view.
        // in the contextual view, this item does not exist so using `context.update()` will not work, 
        // changing it referentially in this way will apply the change to its origin.
        context.getItem().setAmount(8);
    }

}
```

When you apply this change, **the item for those who open the container again** will be 8.

#### Staticity
Before starting your creations, understand that, [View](https://github.com/SaiintBrisson/inventory-framework/blob/master/src/main/java/me/saiintbrisson/minecraft/View.java) is a **static view** of your inventory, any item defined in the builder is automatically static in relation to the container but changeable in relation to the item itself.

Once defined, the item's position cannot be updated but you can still change its nature such as type and quantity.

## Player data
It is possible to define player data, this data was available within the menus, for example:
> *"A player has run `/profile SomeOtherPlayer`, it will open a menu with the information of the player that
> he typed in the name, but how do we get this information inside the menu?"*

It is simple, before the menu opens you can define the data that will be sent to the menu that **will be available only in the context of that player**.
```java
final View view = ...
final String otherPlayer = ...
view.open(player, ImmutableMap.of("target", otherPlayer));
```
And there, within any handler of the menu you can access this. For example, defining a dynamic title using onOpen.
```java
@Override
protected void onOpen(final PreRenderViewContext context) {
    context.setInventoryTitle("Profile of " + context.get("target"));
}
```

## Menu Handlers
Menu handlers are global menu handlers, they occur every time every X action of any player, it is good to use it to standardize things like sending a message to a player or playing a sound.

### Open Handler
Called when a player opens the view.\
In this handler it is possible to define the inventory title that will appear to the player using `context.setInventoryTitle`.\
<br>
Therefore, it is possible to create inventories with **dynamic player-dependent titles**, for example, whenever the inventory opens for a player to display his clan name.
```java
public final class PlaySoundOnOpenView extends View {
        
    public PlaySoundOnOpenView() {
        // as we're going to change the title on onOpen, we don't need to pass a title here.
        super(1);
    }

    @Override
    protected void onOpen(final PreRenderViewContext context) {
        final Player player = context.getPlayer();
        player.playSound(player.getLocation(), Sound.CHEST_OPPEN, 1f, 1f);
        context.setInventoryTitle("Hi, " + player.getName());
    }

}
```

And this event is also cancelable, that is, it is used to prevent the player from opening the inventory if he does 
not have a specific condition. It is called after the items are rendered but after defining the player data for that 
inventory then you can use `context.get` to get a previously defined data. or define a data to be obtained later in the rendering function.
```java
@Override
protected void onOpen(final PreRenderViewContext context) {
    final Player player = context.getPlayer();
    final boolean condition = context.get("condition-key");
    if (!condition) {
        player.sendMessage("Ops... :(");
        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
        context.setCancelled(true);
        return;
    }
    
    context.setInventoryTitle("Hi, " + player.getName());
}
```

### Close Handler
This handler is called when a player closes the inventory, it is not cancelable.
```java
public final class PlaySoundOnCloseView extends View {
        
    public PlaySoundOnCloseView() {
        super(1, "Just play a sound");
    }

    @Override
    protected void onClose(final ViewContext context) {
        final Player player = context.getPlayer();
        player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 1f, 1f);
        player.sendMessage("Bye, " + player.getName() + "... :(");
    }

}
```

## Hooking into
You can get the latest version available on [JitPack](https://jitpack.io/#SaiintBrisson/inventory-framework).

### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.SaiintBrisson.inventory-framework:api:VERSION'
}
```

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
    
<dependency>
    <groupId>com.github.SaiintBrisson</groupId>
    <artifactId>inventory-framework</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

## Recommended Use
We strongly recommend that you **do not shading the inventory-framework** and use it as a plugin, as a dependency on your plug-ins.
You can download a stable version from the [releases](https://github.com/SaiintBrisson/inventory-framework/releases) tab.
