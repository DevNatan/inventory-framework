# inventory-framework
![badge](https://jitpack.io/v/DevNatan/inventory-framework.svg)

Bukkit inventory framework used in some of my projects, feel free to use it. Learn by yourself.

## Setup
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