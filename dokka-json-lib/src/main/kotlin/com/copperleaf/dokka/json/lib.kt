package com.copperleaf.dokka.json

import com.copperleaf.dokka.json.models.KotlinClassDoc
import com.copperleaf.dokka.json.models.KotlinPackageDoc
import com.copperleaf.dokka.json.models.KotlinRootdoc
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Executors

interface KotlindocInvoker {
    fun getRootDoc(sourceDirs: List<Path>, args: List<String>, callback: (InputStream) -> Runnable): KotlinRootdoc
    fun loadCachedRootDoc(): KotlinRootdoc?
}

class KotlindocInvokerImpl(
        private val mavenResolver:  MavenResolver,
        private val dokkaOutputPath: Path,
        private val targets: List<Artifact>
) : KotlindocInvoker {

    override fun getRootDoc(sourceDirs: List<Path>, args: List<String>, callback: (InputStream) -> Runnable): KotlinRootdoc {
        val dokkaJarPaths = mavenResolver.getMavenJars(targets)
        executeDokka(dokkaJarPaths, sourceDirs, args) { callback(it) }
        return getKotlinRootdoc()
    }

    override fun loadCachedRootDoc(): KotlinRootdoc? {
        return if(Files.exists(dokkaOutputPath) && dokkaOutputPath.toFile().list().isNotEmpty()) {
            getKotlinRootdoc()
        }
        else {
            null
        }
    }

// Run Dokka
//----------------------------------------------------------------------------------------------------------------------

    private fun executeDokka(
            dokkaJarPath: List<Artifact>,
            sourceDirs: List<Path>,
            args: List<String>,
            callback: (InputStream) -> Runnable
    ) {
        val processArgs = arrayOf(
                "java",
                "-classpath", dokkaJarPath.map { it.jarPath!!.toFile().absolutePath }.joinToString(File.pathSeparator), // classpath of downloaded jars
                "org.jetbrains.dokka.MainKt", // Dokka main class
                "-format", "json", // JSON format (so we can pick up results afterwards)
                "-noStdlibLink",
                "-impliedPlatforms", "JVM",
                "-src", sourceDirs.map { it.toFile().absolutePath }.joinToString(separator = File.pathSeparator), // the sources to process
                "-output", dokkaOutputPath.toFile().absolutePath, // where Orchid will find them later
                *args.toTypedArray() // allow additional arbitrary args
        )

        val process = ProcessBuilder()
                .command(*processArgs)
                .start()

        Executors.newSingleThreadExecutor().submit(callback(process.inputStream))
        process.waitFor()
    }

// Process Dokka output to a model Orchid can use
//----------------------------------------------------------------------------------------------------------------------

    private fun getKotlinRootdoc(): KotlinRootdoc {
        val packages = getDokkaPackagePages()
        val classes = getDokkaClassPages()

        return KotlinRootdoc(
                packages,
                classes
        )
    }

    private fun getDokkaPackagePages(): List<KotlinPackageDoc> {
        val packagePagesList = ArrayList<KotlinPackageDoc>()
        dokkaOutputPath
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name == "index.json" }
                .map { KotlinPackageDoc.fromJson(it.readText()) }
                .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getDokkaClassPages(): List<KotlinClassDoc> {
        val classPagesList = ArrayList<KotlinClassDoc>()
        dokkaOutputPath
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name != "index.json" }
                .map { KotlinClassDoc.fromJson(it.readText()) }
                .toCollection(classPagesList)

        return classPagesList
    }

}
