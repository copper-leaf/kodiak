# Kodiak

A collection wrappers around various code documentation tools which produces a common JSON output readable by Orchid.

## What is this?

Most code documentation tools work by generating an HTML site. They have to do a lot of work to format navigate the 
code's internal structure, and then create a website that very often, looks quite awful. It would be better for everyone
if the language designers only needed to produce a code model, and then let someone else do the hard work of turning 
that model into a website.

[Orchid](https://orchid.netlify.com/) is that tool that creates a beautiful website for your code docs. This project is
the other side of that coin which produces a model that Orchid can use to generate those sites. 

### Short-Term Goal

In the short-term, this project contains a series of formatters for various languages, which each can be mapped to a 
specific Orchid plugin. 

### Mid-Term Goal

In the mid-term, this project will create a common interface to the code models created by all the individual language
formatters. Orchid will then be updated to read from this common model, rather than the specific language models. This 
will aid in maintainability, and also make it easier for Orchid to support new languages in the future. 

### Long-Term Goal

In the long-term, this project will not only create formatters for individual languages, but also create a 
_specification_ for that code documentation model so that any tool can generate JSON matching that specification and 
Orchid will be able to read it. This project will then create and document that specification and provide several tools
which conform to that spec, but other tools are free to create their own implementations, and Orchid will use it just
the same. 

## Project Structure

Each language in this project contains 3 individual projects, as follows:

- `{language}/{language}-models`
- `{language}/{language}-formatter`
- `{language}/{language}-runner`

These packages are related like so:

- `-formatter` contains a compile dependency on `-models`, and is compiled as a fat jar
- `-runner` contains a compile dependency on `-models`, and is used like a library. It also embeds the `-formatter` fat 
    jar in its project resources. 
- `-models` only contains model classes and functions to serialize/deserialize them

The `-formatter` project is a fat jar, embedding all of its dependencies and runtimes needed to run. It's role is to run
some task which will produce a code model in JSON format using the `-models` format, written to files on disk. It is 
designed to be run completely in isolation, as a CLI tool or started from another process.

The `-runner` project is a lightweight wrapper around the `-formatter` project. It embeds the `-formatter` fat jar in 
its resources, and when requested will cache that jar to the local filesystem and run it as a separate process. It will
then use the same `-models` project to read the written JSON files and return them as a model to the application calling
this library.

The `-models` project uses Kotlinx serialization to create an interface between the `-formatter` and `-runner` projects.
It should contain no logic, and just creates the language-specific models.

## Usage

The general usage for any of these formatters looks like like the following. See the 
[official docs](https://copper-leaf.github.io/kodiak) for specifics for each language.

1) Include the `-runner` dependency in your app:

```groovy
repositories {
    jcenter()
}
dependencies {
    compile 'com.eden:{language}-runner:{latest version}'
}
```

2) Call the wrapper library and get back the code model. The formatter will run synchronously; for async work you will 
    need to start and run it all on a background thread.

```kotlin
// create the runner, and give it a cache directory
var cacheDir: Path = Files.createTempDirectory("cacheDir")
val runner: {language}Invoker = {language}InvokerImpl(cacheDir)

// create the output directory, and make sure to clear it between runs
val outputDir = File("outputDir").canonicalFile.toPath()
outputDir.toFile().deleteRecursively()
outputDir.toFile().mkdirs()

// run the formatter, and get back a documentation model
val rootDoc = runner.getRootDoc(
    listOf( // set one or more directories containing source code files
        File("sourceDir1").canonicalFile.toPath(),
        File("sourceDir2").canonicalFile.toPath()
    ),
    outputDir // tell it where to write the results
) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) } // handle STDOUT streams from the external process

// do whatever you want with the model
```

## Build and Test

Each project is set up such that running `build` on the `-runner` project of each language will build and check 
complete functionality for that language's formatter.

- `./gradlew clean :javadoc:javadoc-runner:build`
- `./gradlew clean :dokka:dokka-runner:build`
- `./gradlew clean :groovydoc:groovydoc-runner:build`

Test source files for each language should go in the appropriate `-formatter` `test/` sourceroot. 

- `:javadoc:javadoc-runner` tests sources from:
    - `javadoc/javadoc-formatter/test/java`
- `:dokka:dokka-runner` tests sources from:
    - `dokka/dokka-formatter/test/java`
    - `dokka/dokka-formatter/test/kotlin`
- `:groovydoc:groovydoc-runner` tests sources from:
    - `groovydoc/groovydoc-formatter/test/java`
    - `groovydoc/groovydoc-formatter/test/groovy`

To build and serve docs locally:

- `./gradlew :docs:orchidServe`