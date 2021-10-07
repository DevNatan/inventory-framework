# inventory-framework

Bukkit inventory framework used in some of my projects, feel free to use it. Learn by yourself.

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