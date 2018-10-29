package com.copperleaf.javadoc.json

import com.copperleaf.dokka.json.Artifact
import com.copperleaf.dokka.json.MavenResolver
import com.copperleaf.javadoc.json.models.JavaClassDoc
import com.copperleaf.javadoc.json.models.JavaPackageDoc
import com.copperleaf.javadoc.json.models.JavadocRootdoc
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Executors

interface JavadocdocInvoker {
    fun getRootDoc(
            sourceDirs: List<Path>,
            startMemory: String = "256m",
            maxMemory: String = "1024m",
            args: List<String> = emptyList(),
            callback: (InputStream) -> Runnable
    ): JavadocRootdoc?

    fun loadCachedRootDoc(): JavadocRootdoc?
}

class JavadocdocInvokerImpl(
        private val mavenResolver: MavenResolver,
        private val javadocOutputPath: Path,
        private val targets: List<Artifact>
) : JavadocdocInvoker {

    override fun getRootDoc(
            sourceDirs: List<Path>,
            startMemory: String,
            maxMemory: String,
            args: List<String>,
            callback: (InputStream) -> Runnable
    ): JavadocRootdoc? {
        val javadocJarPaths = mavenResolver.getMavenJars(targets)
        val success = executeJavadoc(javadocJarPaths, sourceDirs, startMemory, maxMemory, args) { callback(it) }
        return if (success) getJavadocRootdoc() else null
    }

    override fun loadCachedRootDoc(): JavadocRootdoc? {
        return if (Files.exists(javadocOutputPath) && javadocOutputPath.toFile().list().isNotEmpty()) {
            getJavadocRootdoc()
        }
        else {
            null
        }
    }

// Run Javadoc
//----------------------------------------------------------------------------------------------------------------------

    private fun executeJavadoc(
            javadocJarPath: List<Artifact>,
            sourceDirs: List<Path>,
            startMemory: String,
            maxMemory: String,
            args: List<String>,
            callback: (InputStream) -> Runnable
    ): Boolean {
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
                "-d", javadocOutputPath.toFile().absolutePath, // where Orchid will find them later
                "-doclet", "com.copperleaf.javadoc.json.JavadocJsonDoclet",
                "-docletpath", javadocJarPath.map { it.jarPath!!.toFile().absolutePath }.joinToString(File.pathSeparator), // classpath of downloaded jars
                *args.toTypedArray(), // allow additional arbitrary args
                *allFiles.toTypedArray() // the sources to process
        )

        val process = ProcessBuilder()
                .command(*processArgs)
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start()

        val executor = Executors.newSingleThreadExecutor()
        executor.submit(callback(process.inputStream))
        val exitValue = process.waitFor()
        executor.shutdown()
        return exitValue == 0
    }

// Process Javadoc output to a model Orchid can use
//----------------------------------------------------------------------------------------------------------------------

    private fun getJavadocRootdoc(): JavadocRootdoc {
        val packages = getJavadocPackagePages()
        val classes = getJavadocClassPages()

        return JavadocRootdoc(
                packages,
                classes
        )
    }

    private fun getJavadocPackagePages(): List<JavaPackageDoc> {
        val packagePagesList = ArrayList<JavaPackageDoc>()
        javadocOutputPath
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name == "index.json" }
                .map { JavaPackageDoc.fromJson(it.readText()) }
                .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getJavadocClassPages(): List<JavaClassDoc> {
        val classPagesList = ArrayList<JavaClassDoc>()
        javadocOutputPath
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name != "index.json" }
                .map { JavaClassDoc.fromJson(it.readText()) }
                .toCollection(classPagesList)

        return classPagesList
    }

}
