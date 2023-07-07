# AnnotatedConfigs
[![Release](https://img.shields.io/badge/release-0.1.1-blue?style=for-the-badge)](https://github.com/TheXSVV/AnnotatedConfigs/releases/tag/v0.1.1) [![Language](https://img.shields.io/badge/Language-Java-orange?style=for-the-badge)](https://java.com) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/076f23bbe6924bbd8d2972b75cf91b24)](https://app.codacy.com/gh/TheXSVV/AnnotatedConfigs/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

Configs based on annotations for Java

## Supported languages:
* JSON
* Yaml (supported later)

## Installation:
To add a dependency using `Gradle Groovy`, use the following:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.TheXSVV:AnnotatedConfigs:0.1.2'
}
```

To add a dependency using `Gradle Kotlin DSL`:
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.TheXSVV:AnnotatedConfigs:0.1.2")
}
```

To add a dependency using `Maven`:
```xml
<repositories>
  <repository>
    <id>jitpack</id>
    <url>ttps://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.TheXSVV</groupId>
    <artifactId>AnnotatedConfigs/artifactId>
    <version>0.1.2</version>
  </dependency>
</dependencies>
```

If you use the built-in build system in your IDE just download [.jar file from releases](https://github.com/TheXSVV/AnnotatedConfigs/releases) and add it to your project

## Example usage

JSON File:
```json
{
    "custom_name": "name",
    "amount": 5,
    "custom_boss": {
        "name": "Foo"
    },
    "bosses": [
        {
            "name": "Boss 1"
        },
        {
            "name": "Boss 2"
        }
    ]
}
```

Config class:
```java
import code.thexsvv.annotatedconfigs.Configuratable;
import code.thexsvv.annotatedconfigs.annotations.ACLanguage;
import code.thexsvv.annotatedconfigs.ConfigLang;
import code.thexsvv.annotatedconfigs.annotations.ACKey;
import java.util.List;

@ACLanguage(language=ConfigLang.JSON)
public class Config extends Configuratable {
    @ACKey(key="custom_name")
    public String name;
    
    @ACKey
    public int amount;
    
    @ACKey(key="custom_boss")
    public Boss boss;
    
    @ACKey
    public List<Boss> bosses;
}
```

Another configurable class (language will be selected from another Configurable class):
```java
import code.thexsvv.annotatedconfigs.Configuratable;
import code.thexsvv.annotatedconfigs.annotations.ACKey;

public class Boss extends Configuratable {
    @ACKey
    public String name;
}
```

Main class:
```java
import java.io.File;

public class Main {

    public static void main(String[] args) {
        Config config = new Config(); // Creating a config object
        config.load(new File("test.json")); // Loading config from file
        config.load(Main.class.getResourceAsStream("/test.json")); // Loading config from InputStream
        
        System.out.println(config.name);
        System.out.println(config.amount);
        System.out.println(config.boss.name);
        System.out.println(config.bosses.size());
    }
}
```

Output:
```
name
5
Foo
2
```

## Great combination
The `lombok` is perfectly compatible with this library, you can use `@FieldDefaults` and `@Getter`
Real example:
```java
import code.thexsvv.annotatedconfigs.Configuratable;
import lombok.FieldDefaults;
import lombok.AccessLevel;
import lombok.Getter;

@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
public class Config extends Configurable {
    @ACKey
    String name; // This field is automatically sets the private access level
}
```

## Building from sources
1. Clone the repo
`git clone https://github.com/TheXSVV/AnnotatedConfigs.git`
2. Build the project
`gradle build`
