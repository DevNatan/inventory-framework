# inventory-framework
[![](https://jitpack.io/v/DevNatan/inventory-framework.svg)](https://jitpack.io/#DevNatan/inventory-framework/43074d61ba)

## Contents
* [Project Setup](#project-setup)
* [Basic view](#basic-view)
* [Player data](#player-data)
* Virtual Context
* [Menu handlers](#menu-handlers)
  * [Open handler](#open-handler)
  * [Close handler](#close-handler)
  * Render handler
* Item handlers
  * Click handler
  * Render handler
  * Update handler

## Project Setup
##### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.DevNatan.inventory-framework:api:VERSION'
}
```

##### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
    
<dependency>
    <groupId>com.github.DevNatan</groupId>
    <artifactId>inventory-framework</artifactId>
    <version>VERSION</version>
</dependency>
```

## Basic View
This is a basic view, it only displays one item in row 3 of column 4 and is 6 lines in size (the size of the inventory).\
When the player opens an item will be displayed and if clicked the **event will be canceled**
preventing the player from removing it from the inventory or moving it, through `cancelOnClick`.
```java
public final class BasicView extends View {
        
    public MyView() {
        super(6, "Basic view title");
        slot(4, 3).withItem(ItemBuilder.create(Material.DIAMOND)
            .name("This is a basic view.")
            .build()
        ).cancelOnClick();
    }

}
```

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
```
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