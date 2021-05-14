---
---

# kodiak

A collection of wrappers around various code documentation tools which produces a common JSON output readable by Orchid.

![GitHub release (latest by date)](https://img.shields.io/github/v/release/copper-leaf/kodiak)
![Maven Central](https://img.shields.io/maven-central/v/io.github.copper-leaf/kodiak-core)
![Kotlin Version](https://img.shields.io/badge/Kotlin-1.4.32-orange)

## Overview

Most code documentation tools work by generating an HTML site. They have to do a lot of work to format navigate the
code's internal structure, and then create a website that very often, looks quite awful. It would be better for everyone
if the language designers only needed to produce a code model, and then let someone else do the hard work of turning
that model into a website.

[Orchid](https://orchid.netlify.com/) is that tool that creates a beautiful website for your code docs. This project is
the other side of that coin which produces a model that Orchid can use to generate those sites.

## Installation

```kotlin
repositories {
    mavenCentral()
}

// for plain JVM projects
dependencies {
    implementation("com.eden.kodiak:javadoc-runner:{{ site.version }}")
    implementation("com.eden.kodiak:dokka-runner:{{ site.version }}")
    implementation("com.eden.kodiak:groovydoc-runner:{{ site.version }}")
    implementation("com.eden.kodiak:swiftdoc-runner:{{ site.version }}")
}
```

## Targets

### Java (Javadoc)

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("com.eden.kodiak:javadoc-runner:{{ site.version }}")
}
```

```kotlin
var cacheDir: Path = Files.createTempDirectory("javadocCache")
val runner: JavadocInvoker = JavadocInvokerImpl(cacheDir)

val outputDir = File("build/javadoc").canonicalFile.toPath()
outputDir.toFile().deleteRecursively()
outputDir.toFile().mkdirs()

val rootDoc = runner.getRootDoc(
    listOf(
        File("src/main/java").canonicalFile.toPath()
    ),
    outputDir
) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

rootDoc.packages.forEach { processPackage(it) }
rootDoc.classes.forEach { processClass(it) }
```

- skip by including `@suppress` in its comments
    - classes
    - constructors
    - fields
    - methods

- [Javadoc](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javadoc.html)

### Kotlin (Dokka)

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("com.eden.kodiak:dokka-runner:{{ site.version }}")
}
```

```kotlin
var cacheDir: Path = Files.createTempDirectory("dokkaCache")
val runner: KotlindocInvoker = KotlindocInvokerImpl(cacheDir)

val outputDir = File("build/dokka").canonicalFile.toPath()
outputDir.toFile().deleteRecursively()
outputDir.toFile().mkdirs()

val rootDoc = runner.getRootDoc(
    listOf(
        File("src/main/java").canonicalFile.toPath(),
        File("src/main/kotlin").canonicalFile.toPath()
    ),
    outputDir
) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

rootDoc.packages.forEach { processPackage(it) }
rootDoc.classes.forEach { processClass(it) }
```

- skip by including `@suppress` in its comments
    - classes
    - constructors
    - fields
    - methods
    
- [Dokka](https://github.com/Kotlin/dokka)

### Groovy (Groovydoc)

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("com.eden.kodiak:groovydoc-runner:{{ site.version }}")
}
```

```kotlin
var cacheDir: Path = Files.createTempDirectory("groovydocCache")
val runner: GroovydocInvoker = GroovydocInvokerImpl(cacheDir)

val outputDir = File("build/groovydoc").canonicalFile.toPath()
outputDir.toFile().deleteRecursively()
outputDir.toFile().mkdirs()

val rootDoc = runner.getRootDoc(
    listOf(
        File("src/main/java").canonicalFile.toPath(),
        File("src/main/groovy").canonicalFile.toPath()
    ),
    outputDir
) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

rootDoc.packages.forEach { processPackage(it) }
rootDoc.classes.forEach { processClass(it) }
```

- skip by including `@suppress` in its comments
    - classes
    - constructors
    - fields
    - methods

- [Groovydoc](http://docs.groovy-lang.org/docs/next/html/documentation/#_groovydoc_the_groovy_java_documentation_generator)

### Swift (Sourcekitten)

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("com.eden.kodiak:swiftdoc-runner:{{ site.version }}")
}
```

```kotlin
var cacheDir: Path = Files.createTempDirectory("swiftdocCache")
val runner: SwiftdocInvoker = SwiftdocInvokerImpl(cacheDir)

val outputDir = File("build/swiftdoc").canonicalFile.toPath()
outputDir.toFile().deleteRecursively()
outputDir.toFile().mkdirs()

val rootDoc = runner.getRootDoc(
    listOf(
        File("src/main/swift").canonicalFile.toPath()
    ),
    outputDir
) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

rootDoc.sourceFiles.forEach { processSourceFile(it) }
rootDoc.classes.forEach { processClass(it) }
```

- skip by including `- suppress` in its comments
    - classes
    - initializers
    - fields
    - methods

- [SourceKitten](https://github.com/jpsim/SourceKitten)
