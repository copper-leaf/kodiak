---
---

# Javadoc

Uses a Javadoc doclet to produce a JSON format that Orchid can use to generate a documentation site.

## Usage

```kotlin
// build.gradle.kts
repositories {
    jcenter()
}
dependencies {
    compile 'com.eden.kodiak:javadoc-runner:{{ site.version }}'
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

## References

- [Javadoc](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javadoc.html)
