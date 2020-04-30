---
---

# Kotlindoc

Uses a Dokka formatter to produce a JSON format that Orchid can use to generate a documentation site.

## Usage

```kotlin
// build.gradle.kts
repositories {
    jcenter()
}
dependencies {
    compile 'com.eden.kodiak:dokka-runner:{{ site.version }}'
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

## References

- [Dokka](https://github.com/Kotlin/dokka)
