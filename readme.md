# AcroniCore — Spigot Edition

![Platform](https://img.shields.io/badge/Platform-Spigot_|_Paper-orange)
![License](https://img.shields.io/badge/License-LGPLv3-blue)
[![Build and Publish](https://github.com/AcronicalYT/AcroniCore-Spigot/actions/workflows/publish.yml/badge.svg)](https://github.com/AcronicalYT/AcroniCore-Spigot/actions/workflows/publish.yml)

AcroniCore is a modular utility library for Spigot and its forks (Paper, Airplane, etc.). It provides small, focused modules that speed up plugin development by handling common tasks (configuration, commands, databases, GUIs, networking, and more) while remaining lightweight and performant.

Key goals:
- Modular: import only the modules you need.
- Stable: clear separation between release and snapshot artifacts.
- Shadable: designed to be relocated to avoid runtime conflicts.

## Modules
The repository is organised as a multi-project Gradle build. The following modules are currently included (grouped by their parent project as declared in settings.gradle):

- everything — aggregator that builds all modules together

- infrastructure
  - infrastructure:common
  - infrastructure:task-core
  - infrastructure:error-core
  - infrastructure:inject-core
  - infrastructure:updater-core

- backend
  - backend:database-core
    - backend:database-core:sql
    - backend:database-core:mongo
    - backend:database-core:redis
  - backend:config-core
  - backend:pdc-core

- frontend
  - frontend:gui-core
  - frontend:scoreboard-core
  - frontend:hologram-core
  - frontend:particle-core

- mechanics
  - mechanics:command-core
  - mechanics:cooldown-core
  - mechanics:script-core

- environment
  - environment:item-core

- networking
  - networking:nms-core
  - networking:http-core
  - networking:proxy-core
  - networking:discord-core

- development

Notes:
- Only modules explicitly included in settings.gradle are listed above. Several optional or experimental modules are present but commented out in settings.gradle and are not part of the current build.
- Each module lives in its own directory and (where applicable) may contain subprojects, such as the database-core submodules.

## Choosing modules
Recommended approach:
- For most plugins, include only the modules you actually use to keep binary size small.
- Use the `everything` aggregate only for development or if you genuinely need all modules.

## Artifact repository
Stable (release) and snapshot (development) builds are published to the Acronical Maven repository:
- Releases: https://maven.acronical.uk/releases
- Snapshots: https://maven.acronical.uk/snapshots

When depending on a snapshot, ensure the version ends with `-SNAPSHOT` and use the snapshots repository URL.

## Usage examples
Replace [module] and [version] with the module name and version you require.

Maven:

```xml
<repositories>
  <repository>
    <id>acronical-releases</id>
    <url>https://maven.acronical.uk/releases</url>
  </repository>
</repositories>

<dependency>
  <groupId>uk.acronical</groupId>
  <artifactId>[module]</artifactId>
  <version>[version]</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
repositories {
    maven { url = uri("https://maven.acronical.uk/releases") }
}

dependencies {
    implementation("uk.acronical:[module]:[version]")
}
```

Gradle (Groovy):

```groovy
repositories {
    maven { url 'https://maven.acronical.uk/releases' }
}

dependencies {
    implementation 'uk.acronical:[module]:[version]'
}
```

For snapshot artifacts, switch the repository URL to `https://maven.acronical.uk/snapshots` and use a `-SNAPSHOT` version.

## Shading and relocation
If you bundle AcroniCore inside your plugin (recommended for production builds) you must relocate the library packages to avoid conflicts with other plugins using different versions. Use Shadow (Gradle) or Shade (Maven) to create a fat JAR and relocate the `uk.acronical` packages into a vendor namespace.

Gradle (Shadow plugin):

```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

shadowJar {
    relocate 'uk.acronical', 'your.plugin.package.libs.acronical'
}

build.dependsOn shadowJar
```

Maven (maven-shade-plugin):

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.5.1</version>
  <executions>
    <execution>
      <phase>package</phase>
      <goals><goal>shade</goal></goals>
      <configuration>
        <relocations>
          <relocation>
            <pattern>uk.acronical</pattern>
            <shadedPattern>your.plugin.package.libs.acronical</shadedPattern>
          </relocation>
        </relocations>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Notes:
- Replace `your.plugin.package` with your plugin's main package.
- Relocation prevents classpath clashes and runtime NoSuchMethodError/ClassDefFound issues.

## Building from source
To build locally:

Windows PowerShell / Unix shells:

```bash
./gradlew clean build
# or on Windows (git-bash/PowerShell):
./gradlew.bat clean build
```

Artifacts will be available under each module's `build/libs` directory. To publish locally for testing:

```bash
./gradlew publishToMavenLocal
```

## Tests & Documentation
- Unit and integration tests (if present) will be executed as part of the Gradle build in future, **these are yet to be implemented**.
- API documentation is generated with Dokka. You can view the published docs at https://core.acronical.uk or generate them locally.

## Contributing
Contributions are welcome.

Suggested workflow:
1. Fork the repository.
2. Create a feature branch for your changes.
3. Create a pull request with a clear description and test coverage where applicable.

Please follow the existing code style.

## Support
- Discord: https://discord.acronical.uk
- Issues: open an issue on the GitHub repository with reproduction steps.

## Donate
If you find this project useful, consider supporting development: https://ko-fi.com/acronical

## License
AcroniCore is licensed under the LGPLv3. Key points:
- You may use this library in closed-source or commercial plugins.
- You may modify and redistribute AcroniCore itself, but must provide source for modifications to the library per the LGPLv3.
- Plugins that simply depend on AcroniCore do not automatically require their own source to be published.

See the [LICENSE](LICENCE) file for the full text.

---

If you need a specific example for a module or help choosing which modules to include in your plugin, open an issue or ask in Discord.
