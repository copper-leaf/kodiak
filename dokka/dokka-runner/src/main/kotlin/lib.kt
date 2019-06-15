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
        return KotlinModuleDoc(
            getDocsInSubdirectory(destinationDir, "Package", KotlinPackage.serializer()),
            getDocsInSubdirectory(destinationDir, "Class", KotlinClass.serializer())
        )
    }

}
