# inventory-framework
[![](https://jitpack.io/v/SaiintBrisson/inventory-framework.svg)](https://jitpack.io/#SaiintBrisson/command-framework)

### Project Setup
##### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.SaiintBrisson.inventory-framework:api:VERSION'
}
```

##### Gradle (Kotlin DSL)
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.SaiintBrisson.inventory-framework:api:VERSION")
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
    <groupId>com.github.SaiintBrisson.inventory-framework</groupId>
    <artifactId>api</artifactId>
    <version>VERSION</version>
</dependency>
```

### Example
```java
public final class MyView extends View {
        
    private final AtomicInteger counter;

    public MyView() {
        super(6, "Diamond counter");
        counter = new AtomicInteger();
        slot(3, 3).withItem(ItemBuilder.create(Material.DIAMOND)
            .name("Click to see how many players have clicked here!")
            .build()
        ).onUpdate((ctx, $) -> {
            ctx.setItemDisplayName(counter + " players have already clicked on the diamond!");
        }).onClick((ctx, $) -> {
            counter.incrementAndGet();
            ctx.update();
        });
    }

}
```
```
// using the View instance
view.open(player)
```