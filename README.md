# Kodiak

> A collection of wrappers around various code documentation tools which produces a common JSON output readable by Orchid.

![GitHub release (latest by date)](https://img.shields.io/github/v/release/copper-leaf/kodiak)
![Maven Central](https://img.shields.io/maven-central/v/io.github.copper-leaf/kodiak-core)
![Kotlin Version](https://img.shields.io/badge/Kotlin-1.4.32-orange)

# Supported Platforms/Features

| Target Language | Documentation Tool |
| --------------- | ------------------ |
| Java            | Javadoc            |
| Kotlin          | Dokka              |
| Groovy          | Groovydoc          |
| Swift           | SourceKitten       |

# Installation

```kotlin
repositories {
    mavenCentral()
}

// for plain JVM or Android projects
dependencies {
    implementation("io.github.copper-leaf:kodiak-core:{{site.version}}")
}

// for multiplatform projects
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.copper-leaf:kodiak-core:{{site.version}}")
            }
        }
    }
}
```

# Documentation

See the [website](https://copper-leaf.github.io/kodiak/) for detailed documentation and usage instructions.

# License 

kodiak is licensed under the BSD 3-Clause License, see [LICENSE.md](https://github.com/copper-leaf/kodiak/tree/master/LICENSE.md). 

# References

- [SRML](https://github.com/jasonwyatt/SRML)
