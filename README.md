# inventory-framework
![badge](https://jitpack.io/v/DevNatan/inventory-framework.svg)

Bukkit inventory framework used in some of my projects, feel free to use it. Learn by yourself.

## Setup
#### KotlinScript
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.DevNatan.inventory-framework:VERSION")
}
```

#### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.DevNatan.inventory-framework:VERSION'
}
```

#### Maven
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
    <scope>provided</scope>
</dependency>
```

## How to use
Inventory-framework was first developed on 1.8.8, but you can use it with any version.
If there's any issue with an different version, please report it in issues section.

#### Making a simple inventory:
Our view is a simple inventory, with a title and a list of items.

```java
public class MyView extends View {
    
    public MyView() {
        super(3, "My view"); // 3 views per page with title "My view"
    }
    
    @Override
    public void render(ViewContext context) {
        Player player = context.getPlayer(); // We can get the player from inventory.
        
        context.slot(14, new ItemStack(Material.STONE)); // We put the Stone in slot 14. (We written 14 because our array starts at 1)
        context.slot(1, 5, new ItemStack(Material.STONE)); // Also, we can put the Stone in row 1, column 5.
        
        ViewItem myItem = context.slot(3, 5, new ItemStack(Material.STONE)); // We can also put the Stone in row 3, column 5.
        
        myItem.onClick(() -> { // We can add a click event.
            player.sendMessage("You clicked on the stone!");
        });
    }
}
```

There's a lot of things you can do with inventory-framework, but for now, we'll just use the simple one.
You can make paginated pages, refreshing items and schedule tasks inside each view.

After making our view, we need to register it.

```java
public class MyPlugin extends JavaPlugin {
    
    private ViewFrame viewFrame;
    
    @Override
    public void onEnable() {
        // First we need to initialize our facade class to store our views, contexts and data.
        viewFrame = new ViewFrame(this);
        
        viewFrame.register(MyView.class); // We register our view.
    } 
}
```

To open our view, we need to call the facade method.

```java
ViewFrame#open(MyView.class, player); // We open to the right player.
```

You can also open to a specific player with specific data.

```java
Map<String, Object> data = new HashMap<>();

data.put("My data", "My value");

ViewFrame#open(MyView.class, player, data); // We open to the right player with specific data.
```

And then, we use it in our view:

```java
String data = ViewContext#get("My data"); // We get the data from the context which was passed to the open method.
```
