package com.copperleaf.kodiak.groovy

import com.copperleaf.kodiak.common.BaseDocInvoker
import com.copperleaf.kodiak.common.DocInvokerDescriptor
import com.copperleaf.kodiak.common.modules.ModuleLocator
import com.copperleaf.kodiak.common.modules.impl.modulelocator.GradleModuleLocator
import com.copperleaf.kodiak.common.modules.impl.modulelocator.MavenModuleLocator
import com.copperleaf.kodiak.common.version
import com.copperleaf.kodiak.groovy.models.GroovyClass
import com.copperleaf.kodiak.groovy.models.GroovyModuleDoc
import com.copperleaf.kodiak.groovy.models.GroovyPackage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class GroovydocInvokerImpl(
    cacheDir: Path = Files.createTempDirectory("groovydoc-runner"),
    private val startMemory: String = "256m",
    private val maxMemory: String = "1024m"
) : BaseDocInvoker<GroovyModuleDoc>(cacheDir) {
    override val formatterJarName = "groovydoc-formatter-$version-all"

    override fun describe(): DocInvokerDescriptor {
        return DocInvokerDescriptor(
            ModuleLocator.from(GradleModuleLocator(), MavenModuleLocator()),
            listOf("groovy", "java")
        )
    }

    override fun createProcessArgs(sourceDirs: List<Path>, destinationDir: Path, args: List<String>): Array<String> {
        return arrayOf(
            "java",
            "-Xms$startMemory", "-Xmx$maxMemory",
            "-classpath", formatterJar.toFile().absolutePath, // classpath of embedded formatter jar
            "com.copperleaf.kodiak.groovy.MainKt", // Groovydoc Formatter main class
            "--src",
            sourceDirs.map { it.toFile().absolutePath }
                .joinToString(separator = File.pathSeparator), // the sources to process
            "--output", destinationDir.toFile().absolutePath, // where Orchid will find them later
            *args.toTypedArray() // allow additional arbitrary args
        )
    }

    override fun loadModuleDocFromDisk(destinationDir: Path): GroovyModuleDoc {
        return GroovyModuleDoc(
            getDocsInSubdirectory(destinationDir, "Package", GroovyPackage.serializer()),
            getDocsInSubdirectory(destinationDir, "Class", GroovyClass.serializer())
        )
    }
}
