package com.copperleaf.dokka.json

import com.copperleaf.dokka.json.models.KotlinClass
import com.copperleaf.dokka.json.models.KotlinPackage
import com.copperleaf.dokka.json.models.KotlinRootDoc
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
    ): KotlinRootDoc?

    fun loadCachedRootDoc(destinationDir: Path): KotlinRootDoc?
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
            callback: (InputStream) -> Runnable): KotlinRootDoc? {
        val success = executeDokka(sourceDirs, destinationDir, args) { callback(it) }
        return if (success) getKotlinRootdoc(destinationDir) else null
    }

    override fun loadCachedRootDoc(destinationDir: Path): KotlinRootDoc? {
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
            this.javaClass.getResourceAsStream("/dokka-formatter-all.zip"),
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

    private fun getKotlinRootdoc(destinationDir: Path): KotlinRootDoc {
        val packages = getDokkaPackagePages(destinationDir)
        val classes = getDokkaClassPages(destinationDir)

        return KotlinRootDoc(
                packages,
                classes
        )
    }

    private fun getDokkaPackagePages(destinationDir: Path): List<KotlinPackage> {
        val packagePagesList = ArrayList<KotlinPackage>()
        destinationDir
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name == "index.json" }
                .map { KotlinPackage.fromJson(it.readText()) }
                .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getDokkaClassPages(destinationDir: Path): List<KotlinClass> {
        val classPagesList = ArrayList<KotlinClass>()
        destinationDir
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name != "index.json" }
                .map { KotlinClass.fromJson(it.readText()) }
                .toCollection(classPagesList)

        return classPagesList
    }

}
