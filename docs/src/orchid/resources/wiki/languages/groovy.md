---
---

# Groovydoc

Uses a Groovy's Groovydoc APIs to produce a JSON format that Orchid can use to generate a documentation site.

## Usage

```groovy
// build.gradle
repositories {
    jcenter()
}
dependencies {
    compile 'com.eden:groovydoc-runner:{latest version}'
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

## References

- [Groovydoc](http://docs.groovy-lang.org/docs/next/html/documentation/#_groovydoc_the_groovy_java_documentation_generator)
