package com.copperleaf.javadoc.json

import com.copperleaf.dokka.json.Artifact
import com.copperleaf.dokka.json.MavenResolver
import com.copperleaf.javadoc.json.models.JavadocClassDoc
import com.copperleaf.javadoc.json.models.JavadocPackageDoc
import com.copperleaf.javadoc.json.models.JavadocRootdoc
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Executors

interface JavadocdocInvoker {
    fun getRootDoc(sourceDirs: List<Path>, args: List<String>, callback: (InputStream) -> Runnable): JavadocRootdoc
    fun loadCachedRootDoc(): JavadocRootdoc?
}

class JavadocdocInvokerImpl(
        private val mavenResolver: MavenResolver,
        private val javadocOutputPath: Path,
        private val targets: List<Artifact>
) : JavadocdocInvoker {

    override fun getRootDoc(sourceDirs: List<Path>, args: List<String>, callback: (InputStream) -> Runnable): JavadocRootdoc {
        val javadocJarPaths = mavenResolver.getMavenJars(targets)
        executeJavadoc(javadocJarPaths, sourceDirs, args) { callback(it) }
        return getJavadocRootdoc()
    }

    override fun loadCachedRootDoc(): JavadocRootdoc? {
        return if(Files.exists(javadocOutputPath) && javadocOutputPath.toFile().list().isNotEmpty()) {
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
            args: List<String>,
            callback: (InputStream) -> Runnable
    ) {
        val allFiles = mutableListOf<String>()
        sourceDirs.forEach {
            it.toFile().walk().forEach { f ->
                if(f.isFile && f.extension == "java") {
                    allFiles.add(f.absolutePath)
                }
            }
        }

        val processArgs = arrayOf(
                "javadoc",
                "-d", javadocOutputPath.toFile().absolutePath, // where Orchid will find them later
                "-doclet", "com.copperleaf.javadoc.json.JavadocJsonDoclet",
                "-docletpath", javadocJarPath.map { it.jarPath!!.toFile().absolutePath }.joinToString(File.pathSeparator), // classpath of downloaded jars
                *args.toTypedArray(), // allow additional arbitrary args
                *allFiles.toTypedArray() // the sources to process
        )

        val process = ProcessBuilder()
                .command(*processArgs)
                .start()

        Executors.newSingleThreadExecutor().submit(callback(process.inputStream))
        process.waitFor()
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

    private fun getJavadocPackagePages(): List<JavadocPackageDoc> {
        val packagePagesList = ArrayList<JavadocPackageDoc>()
        javadocOutputPath
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name == "index.json" }
                .map { JavadocPackageDoc.fromJson(it.readText()) }
                .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getJavadocClassPages(): List<JavadocClassDoc> {
        val classPagesList = ArrayList<JavadocClassDoc>()
        javadocOutputPath
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name != "index.json" }
                .map { JavadocClassDoc.fromJson(it.readText()) }
                .toCollection(classPagesList)

        return classPagesList
    }

}


/*
 javadoc /Users/cbrooks/Documents/personal/java/eden/dokka-json/javadoc-json/src/test/java/com/copperleaf/dokka/json/test/java/JavaClass.java -d /Users/cbrooks/Documents/personal/java/eden/dokka-json/javadoc-json/build/javadoc/output
 */