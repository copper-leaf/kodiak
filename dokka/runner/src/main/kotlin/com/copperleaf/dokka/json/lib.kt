package com.copperleaf.dokka.json

import com.copperleaf.dokka.json.models.KotlinClassDoc
import com.copperleaf.dokka.json.models.KotlinPackageDoc
import com.copperleaf.dokka.json.models.KotlinRootdoc
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.Executors

interface KotlindocInvoker {
    fun getRootDoc(
            sourceDirs: List<Path>,
            destinationDir: Path,
            args: List<String> = emptyList(),
            callback: (InputStream) -> Runnable
    ): KotlinRootdoc?

    fun loadCachedRootDoc(destinationDir: Path): KotlinRootdoc?
}

class KotlindocInvokerImpl(
    private val cacheDir: Path = Files.createTempDirectory("dokka-runner"),
    private val startMemory: String = "256m",
    private val maxMemory: String = "1024m"
) : KotlindocInvoker {

    val formatterJar = cacheDir.resolve("dokka-formatter-all.jar")

    override fun getRootDoc(
            sourceDirs: List<Path>,
            destinationDir: Path,
            args: List<String>,
            callback: (InputStream) -> Runnable): KotlinRootdoc? {
        println("dokka sourceDirs=    ${sourceDirs.joinToString { it.toFile().canonicalPath }}")
        println("dokka destinationDir=${destinationDir.toFile().canonicalPath }")
        println("dokka cacheDir=      ${cacheDir.toFile().canonicalPath }")
        println("dokka formatterJar=  ${formatterJar.toFile().canonicalPath }")

        val success = executeDokka(sourceDirs, destinationDir, args) { callback(it) }
        return if (success) getKotlinRootdoc(destinationDir) else null
    }

    override fun loadCachedRootDoc(destinationDir: Path): KotlinRootdoc? {
        return if (Files.exists(destinationDir) && destinationDir.toFile().list().isNotEmpty()) {
            getKotlinRootdoc(destinationDir)
        }
        else {
            null
        }
    }

// Run Dokka
//----------------------------------------------------------------------------------------------------------------------

    private fun cacheEmbeddedJar() {
        formatterJar.parent.toFile().mkdirs()
        Files.copy(
            object {}::class.java.getResourceAsStream("/dokka-formatter-all.zip"),
            formatterJar,
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    private fun executeDokka(
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
                "org.jetbrains.dokka.MainKt", // Dokka main class
                "-format", "json", // JSON format (so we can pick up results afterwards)
                "-noStdlibLink",
                "-impliedPlatforms", "JVM",
                "-src", sourceDirs.map { it.toFile().absolutePath }.joinToString(separator = File.pathSeparator), // the sources to process
                "-output", destinationDir.toFile().absolutePath, // where Orchid will find them later
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

// Process Dokka output to a model Orchid can use
//----------------------------------------------------------------------------------------------------------------------

    private fun getKotlinRootdoc(destinationDir: Path): KotlinRootdoc {
        val packages = getDokkaPackagePages(destinationDir)
        val classes = getDokkaClassPages(destinationDir)

        return KotlinRootdoc(
                packages,
                classes
        )
    }

    private fun getDokkaPackagePages(destinationDir: Path): List<KotlinPackageDoc> {
        val packagePagesList = ArrayList<KotlinPackageDoc>()
        destinationDir
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name == "index.json" }
                .map { KotlinPackageDoc.fromJson(it.readText()) }
                .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getDokkaClassPages(destinationDir: Path): List<KotlinClassDoc> {
        val classPagesList = ArrayList<KotlinClassDoc>()
        destinationDir
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name != "index.json" }
                .map { KotlinClassDoc.fromJson(it.readText()) }
                .toCollection(classPagesList)

        return classPagesList
    }

}
