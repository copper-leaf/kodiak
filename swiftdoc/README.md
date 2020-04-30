---
---

# Swiftdoc

Uses a Sourcekitten formatter to produce a JSON format that Orchid can use to generate a documentation site.

## Usage

```kotlin
// build.gradle.kts
repositories {
    jcenter()
}
dependencies {
    compile 'com.eden.kodiak:swiftdoc-runner:{{ site.version }}'
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

## References

- [SourceKitten](https://github.com/jpsim/SourceKitten)
