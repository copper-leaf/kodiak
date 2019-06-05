package com.copperleaf.groovydoc.json

import com.copperleaf.groovydoc.json.models.GroovyClass
import com.copperleaf.groovydoc.json.models.GroovyModuleDoc
import com.copperleaf.groovydoc.json.models.GroovyPackage
import com.copperleaf.json.common.BaseDocInvoker
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class GroovydocInvokerImpl(
    cacheDir: Path = Files.createTempDirectory("groovydoc-runner"),
    private val startMemory: String = "256m",
    private val maxMemory: String = "1024m"
) : BaseDocInvoker<GroovyModuleDoc>(cacheDir) {
    override val formatterJarName = "groovydoc-formatter-all"

    override fun createProcessArgs(sourceDirs: List<Path>, destinationDir: Path, args: List<String>): Array<String> {
        return arrayOf(
            "java",
            "-Xms$startMemory", "-Xmx$maxMemory",
            "-classpath", formatterJar.toFile().absolutePath,    // classpath of embedded formatter jar
            "com.copperleaf.groovydoc.json.MainKt",              // Groovydoc Formatter main class
            "--src", sourceDirs.map { it.toFile().absolutePath }
                .joinToString(separator = File.pathSeparator),   // the sources to process
            "--output", destinationDir.toFile().absolutePath,    // where Orchid will find them later
            *args.toTypedArray()                                 // allow additional arbitrary args
        )
    }

    override fun loadModuleDocFromDisk(destinationDir: Path): GroovyModuleDoc {
        val sourceFiles = getGroovydocPackageDocs(destinationDir)
        val classes = getGroovydocClassDocs(destinationDir)

        return GroovyModuleDoc(
            sourceFiles,
            classes
        )
    }

// Process Javadoc output to a model Orchid can use
//----------------------------------------------------------------------------------------------------------------------

    private fun getGroovydocPackageDocs(destinationDir: Path): List<GroovyPackage> {
        val packagePagesList = ArrayList<GroovyPackage>()
        destinationDir
            .toFile()
            .walkTopDown()
            .filter { it.isFile && it.name == "index.json" }
            .map { GroovyPackage.fromJson(it.readText()) }
            .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getGroovydocClassDocs(destinationDir: Path): List<GroovyClass> {
        val classPagesList = ArrayList<GroovyClass>()
        destinationDir
            .toFile()
            .walkTopDown()
            .filter { it.isFile && it.name != "index.json" }
            .map { GroovyClass.fromJson(it.readText()) }
            .toCollection(classPagesList)

        return classPagesList
    }

}
