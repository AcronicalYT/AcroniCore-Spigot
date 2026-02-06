# Acronicore Spigot

![Platform](https://img.shields.io/badge/Platform-Spigot_|_Paper-orange)
![License](https://img.shields.io/badge/License-LGPLv3-blue)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen)

This version of **AcroniCore** is designed and built for servers running Spigot, or any of its forks (Paper, Airplane, etc.). It provides robust and efficient core plugin libraries to developers which helps speed up the development process without sacrificing on speed, performance and safety.

## Modules
| Module            | Used for                                |
|-------------------|:----------------------------------------|
| **common**        | Maths, Strings, Chat, Utilities, etc.   |
| **command-core**  | Command creation, tab completion (soon) |
| **config-core**   | Config files: yaml, messages            |
| **database-core** | Database connections, queries, etc.*    |
| **gui-core**      | GUI creation and management             |
| **item-core**     | ItemStacks, ItemMeta, etc.              |
| **nms-core**      | NMS interfacing                         |

### Import Options
* **Individual Modules:** Import only what you need (Recommended).
* **Full Suite:** Import all modules using `everything`.
* **Database Specifics:** The `database-core` module has split implementations:
    * `database-core:sql` - For SQL-based databases (HikariCP).
    * `database-core:mongo` - For MongoDB.
    * `database-core:all` - Includes both SQL and Mongo drivers.

---

## Implementation
AcroniCore is available in both **Release** (Stable) and **Snapshot** (Dev) versions.
You can browse available versions at: [https://maven.acronical.uk/](https://maven.acronical.uk/)

### Release
To implement the release version of AcroniCore, you must determine your package manager (Maven, Gradle Kotlin, Gradle Groovy) as the implementation code differs for each. You can find the implementation code for each package manager on the [Maven Repository](https://maven.acronical.uk/).

#### For Maven:
```xml
<repository>
  <id>acronical-repo-releases</id>
  <name>Acronical&#39;s Repos</name>
  <url>https://maven.acronical.uk/releases</url>
</repository>

<dependency>
  <groupId>uk.acronical</groupId>
  <artifactId>YOUR-REQUIRED-MODULE</artifactId>
  <version>VERSION-HERE</version>
  <scope>compile</scope>
</dependency>
```

#### For Gradle Kotlin:
```kotlin
maven {
    name = "acronicalRepoReleases"
    url = uri("https://maven.acronical.uk/releases")
}

implementation("uk.acronical:[module]:[version]")
```

#### For Gradle Groovy:
```groovy
maven {
    name = 'acronicalRepoReleases'
    url = 'https://maven.acronical.uk/releases'
}

implementation 'uk.acronical:[module]:[version]'
```

### Snapshot
Implementing the snapshot version of AcroniCore is the same as implementing the release version, except you must change the repository URL to `https://maven.acronical.uk/snapshots` and ensure that your version ends with `-SNAPSHOT`. You can find the implementation code for each package manager on the [Maven Repository](https://maven.acronical.uk/).

## Examples
These will be coming soon as I am still working on the documentation for the modules.

## Building from Source
If you want to build AcroniCore from source, you can clone the repository and run the `./gradlew build` command in the root directory of the project:

This will build all modules and create the JAR files in the `build/libs` directory of each module. You can then use these JAR files in your project by adding them as dependencies.

Once built, you can also run `./gradlew publishToMavenLocal` to publish the built JAR files to your local Maven repository, allowing you to use them in your project without needing to upload them to a remote repository.

## Contributing
If you want to contribute to AcroniCore, you can fork the repository and create a pull request with your changes. Please ensure that your code follows the existing code style and that you have tested your changes before submitting a pull request. You can also open an issue if you find a bug or have a feature request.

## Support
If you need support or have any questions, you can join [my Discord Server](https://discord.acronical.uk) and ask for help in the appropriate channels. You can also open an issue on the GitHub repository if you find a bug or have a feature request.

## Donate
I appreciate you taking the time to be interested in my project. I've left this message for all of the most dedicated people.
<p><a href="https://ko-fi.com/acronical"> <img align="left" src="https://cdn.ko-fi.com/cdn/kofi3.png?v=3" height="50" width="210" alt="acronical" /></a></p><br><br>

## License
**AcroniCore** is licensed under the **LGPLv3**.

* **You CAN** use this library in your own private, closed-source, or commercial plugins.
* **You CAN** fork and modify this library.
* **You MUST** share the source code of your modifications to **AcroniCore** if you distribute them.
* **You DO NOT** have to share the source code of the plugins that *use* AcroniCore.

See the [LICENSE](LICENSE) file for details.
