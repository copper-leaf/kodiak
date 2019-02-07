package com.copperleaf.groovydoc.json

import com.copperleaf.groovydoc.json.models.GroovydocClassDoc
import com.copperleaf.groovydoc.json.models.GroovydocPackageDoc
import com.copperleaf.groovydoc.json.models.GroovydocRootdoc
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.Executors

interface GroovydocInvoker {
    fun getRootDoc(
        sourceDirs: List<Path>,
        destinationDir: Path,
        args: List<String> = emptyList(),
        callback: (InputStream) -> Runnable
    ): GroovydocRootdoc?

    fun loadCachedRootDoc(destinationDir: Path): GroovydocRootdoc?
}

class GroovydocInvokerImpl(
    private val cacheDir: Path = Files.createTempDirectory("groovydoc-runner"),
    private val startMemory: String = "256m",
    private val maxMemory: String = "1024m"
) : GroovydocInvoker {

    val formatterJar = cacheDir.resolve("groovydoc-formatter.jar")

    override fun getRootDoc(
        sourceDirs: List<Path>,
        destinationDir: Path,
        args: List<String>,
        callback: (InputStream) -> Runnable
    ): GroovydocRootdoc? {
        val success = executeSwiftdoc(sourceDirs, destinationDir, args) { callback(it) }
        return if (success) getGroovydocRootdoc(destinationDir) else null
    }

    override fun loadCachedRootDoc(destinationDir: Path): GroovydocRootdoc? {
        return if (Files.exists(destinationDir) && destinationDir.toFile().list().isNotEmpty()) {
            getGroovydocRootdoc(destinationDir)
        } else {
            null
        }
    }

// Run Groovydoc
//----------------------------------------------------------------------------------------------------------------------

    private fun cacheEmbeddedJar() {
        formatterJar.parent.toFile().mkdirs()
        Files.copy(
            this.javaClass.getResourceAsStream("/groovydoc-formatter-all.zip"),
            formatterJar,
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    private fun executeSwiftdoc(
        sourceDirs: List<Path>,
        destinationDir: Path,
        args: List<String>,
        callback: (InputStream) -> Runnable
    ): Boolean {
        cacheEmbeddedJar()

        val processArgs = arrayOf(
            "java",
            "-Xms$startMemory", "-Xmx$maxMemory",
            "-classpath", formatterJar.toFile().absolutePath, // classpath of embedded formatter jar
            "com.copperleaf.groovydoc.json.MainKt", // Groovydoc Formatter main class
            "--src", sourceDirs.map { it.toFile().absolutePath }.joinToString(separator = File.pathSeparator), // the sources to process
            "--output", destinationDir.toFile().absolutePath, // where Orchid will find them later
            "--cacheDir", cacheDir.toFile().absolutePath, // where Orchid will find them later
            *args.toTypedArray() // allow additional arbitrary args
        )

        val process = ProcessBuilder()
            .command(*processArgs)
            .redirectErrorStream(true)
            .start()

        val executor = Executors.newSingleThreadExecutor()
        executor.submit(callback(process.inputStream))
        val exitValue = process.waitFor()
        executor.shutdown()
        return exitValue == 0
    }

// Process Javadoc output to a model Orchid can use
//----------------------------------------------------------------------------------------------------------------------

    private fun getGroovydocRootdoc(destinationDir: Path): GroovydocRootdoc {
        val sourceFiles = getGroovydocPackageDocs(destinationDir)
        val classes = getGroovydocClassDocs(destinationDir)

        return GroovydocRootdoc(
            sourceFiles,
            classes
        )
    }

    private fun getGroovydocPackageDocs(destinationDir: Path): List<GroovydocPackageDoc> {
        val packagePagesList = ArrayList<GroovydocPackageDoc>()
        destinationDir
            .toFile()
            .walkTopDown()
            .filter { it.isFile && it.name == "index.json" }
            .map { GroovydocPackageDoc.fromJson(it.readText()) }
            .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getGroovydocClassDocs(destinationDir: Path): List<GroovydocClassDoc> {
        val classPagesList = ArrayList<GroovydocClassDoc>()
        destinationDir
            .toFile()
            .walkTopDown()
            .filter { it.isFile && it.name != "index.json" }
            .map { GroovydocClassDoc.fromJson(it.readText()) }
            .toCollection(classPagesList)

        return classPagesList
    }

}
