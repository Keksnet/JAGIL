# JAGIL
Version: 4.0-beta.6

just another generic inventory library (for Spigot)

You have to install [JAGIL-Loader](https://github.com/Keksnet/JAGIL-Loader)
on the server in order for JAGIL to work.

## Important information regarding compatibility

Starting with JAGIL 4.0-beta.6 JAGIL will NOT support spigot or bukkit servers anymore.
For further inside please read the full notice [here](v4-changes.md).

## Maven
#### Repository:
```xml
<repositories>
	<repository>
		<id>repo.neo8.de</id>
		<url>https://repo.neo8.de/</url>
	</repository>
</repositories>
```

#### Dependency:
```xml
<dependency>
    <groupID>de.neo.jagil</groupID>
    <artifactID>JAGIL</artifactID>
    <version>VERSION</version>
    <scope>compile</scope>
</dependency>
```

## Gradle
#### Repository:
```groovy
repositories {
    maven {
        url 'https://repo.neo8.de/'
    }
}
```

#### Dependency:
```groovy
dependencies {
    compileOnly 'de.neo.jagil:JAGIL:VERSION'
}
```


### How to use:
And you have to put the following in your onEnable method:
```java
JAGIL.init(JavaPlugin);
```
If you turn compatibilityMode on it will disable some features like save closing the inventory.

To create a new GUI you have to create a class that extends GUI.
If you like to use functional programming you can use the GUIBuilder
class.

## Help! I do not know how to get started.
Take a look at the [wiki](https://github.com/Keksnet/JAGIL/wiki).
_Wiki coming soon._

## Planned Features for v4
- ~~Animations~~
- ~~Removal of compatibilitymode and other hacky "fixes"~~
- more Json features
- "codeless" features
- Wiki (applies to v3 as well)
- ~~Attributes~~
- UI System
- (Prebuild) UI Elements

If there are any questions please write me on Discord (Neo8#4608).

Have Fun!
