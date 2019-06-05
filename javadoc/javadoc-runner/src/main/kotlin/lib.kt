package com.copperleaf.javadoc.json

import com.copperleaf.javadoc.json.models.JavaClass
import com.copperleaf.javadoc.json.models.JavaPackage
import com.copperleaf.javadoc.json.models.JavaRootDoc
import com.copperleaf.json.common.DocInvoker
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.Executors

class JavadocInvokerImpl(
        private val cacheDir: Path = Files.createTempDirectory("javadoc-runner"),
        private val startMemory: String = "256m",
        private val maxMemory: String = "1024m"
) : DocInvoker<JavaRootDoc> {

    val formatterJar = cacheDir.resolve("javadoc-formatter.jar")

    override fun getModuleDoc(
            sourceDirs: List<Path>,
            destinationDir: Path,
            cliArgs: List<String>,
            callback: (InputStream) -> Runnable
    ): JavaRootDoc? {
        val success = executeJavadoc(sourceDirs, destinationDir, cliArgs) { callback(it) }
        return if (success) getJavadocRootdoc(destinationDir) else null
    }

    override fun loadCachedModuleDoc(destinationDir: Path): JavaRootDoc? {
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
            this.javaClass.getResourceAsStream("/javadoc-formatter-all.zip"),
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

    private fun getJavadocRootdoc(destinationDir: Path): JavaRootDoc {
        val packages = getJavadocPackagePages(destinationDir)
        val classes = getJavadocClassPages(destinationDir)

        return JavaRootDoc(
                packages,
                classes
        )
    }

    private fun getJavadocPackagePages(destinationDir: Path): List<JavaPackage> {
        val packagePagesList = ArrayList<JavaPackage>()
        destinationDir
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name == "index.json" }
                .map { JavaPackage.fromJson(it.readText()) }
                .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getJavadocClassPages(destinationDir: Path): List<JavaClass> {
        val classPagesList = ArrayList<JavaClass>()
        destinationDir
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name != "index.json" }
                .map { JavaClass.fromJson(it.readText()) }
                .toCollection(classPagesList)

        return classPagesList
    }

}
