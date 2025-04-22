# wcommander

Command framework for your Minecraft plugins, bots and more!

## Platform support

| Artifact | Java version | Platform version     |
|----------|--------------|----------------------|
| core     | 1.8          | -                    |
| paper    | 1.8          | 1.16.5-R0.1-SNAPSHOT |
| spigot   | 1.8          | 1.8.8-R0.1-SNAPSHOT  |
| velocity | 11           | 3.2.0-SNAPSHOT       |

## Usage

Add this into your pom.xml:
```xml
<repository>
  <id>github-whilein-wcommander</id>
  <url>https://maven.pkg.github.com/whilein/wcommander</url>
</repository>
```

```xml
<dependency>
  <groupId>io.github.whilein.wcommander</groupId>
  <artifactId>wcommander-PLATFORM</artifactId>
  <version>VERSION</version>
</dependency>
```

Replace `PLATFORM` with platform specified above and `VERSION` with latest version
