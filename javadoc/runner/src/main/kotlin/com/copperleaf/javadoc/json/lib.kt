package com.copperleaf.javadoc.json

import com.copperleaf.javadoc.json.models.JavaClassDoc
import com.copperleaf.javadoc.json.models.JavaPackageDoc
import com.copperleaf.javadoc.json.models.JavadocRootdoc
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.Executors

interface JavadocdocInvoker {
    fun getRootDoc(
            sourceDirs: List<Path>,
            destinationDir: Path,
            args: List<String> = emptyList(),
            callback: (InputStream) -> Runnable
    ): JavadocRootdoc?

    fun loadCachedRootDoc(destinationDir: Path): JavadocRootdoc?
}

class JavadocdocInvokerImpl(
        private val cacheDir: Path = Files.createTempDirectory("javadoc-runner"),
        private val startMemory: String = "256m",
        private val maxMemory: String = "1024m"
) : JavadocdocInvoker {

    val formatterJar = cacheDir.resolve("javadoc-formatter.jar")

    override fun getRootDoc(
            sourceDirs: List<Path>,
            destinationDir: Path,
            args: List<String>,
            callback: (InputStream) -> Runnable
    ): JavadocRootdoc? {
        val success = executeJavadoc(sourceDirs, destinationDir, args) { callback(it) }
        return if (success) getJavadocRootdoc(destinationDir) else null
    }

    override fun loadCachedRootDoc(destinationDir: Path): JavadocRootdoc? {
        return if (Files.exists(destinationDir) && destinationDir.toFile().list().isNotEmpty()) {
            getJavadocRootdoc(destinationDir)
        }
        else {
            null
        }
    }

// Run Javadoc
//----------------------------------------------------------------------------------------------------------------------

    private fun cacheEmbeddedJar() {
        formatterJar.parent.toFile().mkdirs()
        Files.copy(
            object {}::class.java.getResourceAsStream("/javadoc-formatter-all.zip"),
            formatterJar,
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    private fun executeJavadoc(
            sourceDirs: List<Path>,
            destinationDir: Path,
            args: List<String>,
            callback: (InputStream) -> Runnable
    ): Boolean {
        cacheEmbeddedJar()

        val allFiles = mutableListOf<String>()
        sourceDirs.forEach {
            it.toFile().walk().forEach { f ->
                if (f.isFile && f.extension == "java") {
                    allFiles.add(f.absolutePath)
                }
            }
        }

        val processArgs = arrayOf(
                "javadoc",
                "-J-Xms$startMemory", "-J-Xmx$maxMemory",
                "-d", destinationDir.toFile().absolutePath, // where Orchid will find them later
                "-doclet", "com.copperleaf.javadoc.json.JavadocJsonDoclet",
                "-docletpath", formatterJar.toFile().absolutePath, // classpath of embedded formatter jar
                *args.toTypedArray(), // allow additional arbitrary args
                *allFiles.toTypedArray() // the sources to process
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

    private fun getJavadocRootdoc(destinationDir: Path): JavadocRootdoc {
        val packages = getJavadocPackagePages(destinationDir)
        val classes = getJavadocClassPages(destinationDir)

        return JavadocRootdoc(
                packages,
                classes
        )
    }

    private fun getJavadocPackagePages(destinationDir: Path): List<JavaPackageDoc> {
        val packagePagesList = ArrayList<JavaPackageDoc>()
        destinationDir
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name == "index.json" }
                .map { JavaPackageDoc.fromJson(it.readText()) }
                .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getJavadocClassPages(destinationDir: Path): List<JavaClassDoc> {
        val classPagesList = ArrayList<JavaClassDoc>()
        destinationDir
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name != "index.json" }
                .map { JavaClassDoc.fromJson(it.readText()) }
                .toCollection(classPagesList)

        return classPagesList
    }

}
