package com.copperleaf.dokka.json

import com.copperleaf.dokka.json.models.KotlinClass
import com.copperleaf.dokka.json.models.KotlinModuleDoc
import com.copperleaf.dokka.json.models.KotlinPackage
import com.copperleaf.json.common.BaseDocInvoker
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class KotlindocInvokerImpl(
    cacheDir: Path = Files.createTempDirectory("dokka-runner"),
    private val startMemory: String = "256m",
    private val maxMemory: String = "1024m"
) : BaseDocInvoker<KotlinModuleDoc>(cacheDir) {
    override val formatterJarName = "dokka-formatter-all"

    override fun createProcessArgs(sourceDirs: List<Path>, destinationDir: Path, args: List<String>): Array<String> {
        return arrayOf(
            "java",
            "-Xms$startMemory", "-Xmx$maxMemory",
            "-classpath", formatterJar.toFile().absolutePath,   // classpath of embedded formatter jar
            "org.jetbrains.dokka.MainKt",                       // Dokka main class
            "-format", "json",                                  // JSON format (so we can pick up results afterwards)
            "-noStdlibLink",
            "-impliedPlatforms", "JVM",
            "-src", sourceDirs.map { it.toFile().absolutePath }
                .joinToString(separator = File.pathSeparator),  // the sources to process
            "-output", destinationDir.toFile().absolutePath,    // where Orchid will find them later
            *args.toTypedArray()                                // allow additional arbitrary args
        )
    }

    override fun loadModuleDocFromDisk(destinationDir: Path): KotlinModuleDoc {
        val packages = getDokkaPackagePages(destinationDir)
        val classes = getDokkaClassPages(destinationDir)

        return KotlinModuleDoc(
            packages,
            classes
        )
    }

// Process Dokka output to a model Orchid can use
//----------------------------------------------------------------------------------------------------------------------

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
