package com.copperleaf.kodiak.swift

import com.copperleaf.kodiak.common.BaseDocInvoker
import com.copperleaf.kodiak.common.DocInvokerDescriptor
import com.copperleaf.kodiak.common.modules.ModuleLocator
import com.copperleaf.kodiak.common.modules.impl.modulelocator.GradleModuleLocator
import com.copperleaf.kodiak.common.modules.impl.modulelocator.MavenModuleLocator
import com.copperleaf.kodiak.common.version
import com.copperleaf.kodiak.swift.models.SwiftClass
import com.copperleaf.kodiak.swift.models.SwiftModuleDoc
import com.copperleaf.kodiak.swift.models.SwiftSourceFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class SwiftdocInvokerImpl(
    cacheDir: Path = Files.createTempDirectory("swiftdoc-runner"),
    private val startMemory: String = "256m",
    private val maxMemory: String = "1024m"
) : BaseDocInvoker<SwiftModuleDoc>(cacheDir) {
    override val formatterJarName = "swiftdoc-formatter-$version-all"

    override fun describe(): DocInvokerDescriptor {
        return DocInvokerDescriptor(
            ModuleLocator.from(GradleModuleLocator(), MavenModuleLocator()),
            listOf("swift")
        )
    }

    override fun createProcessArgs(sourceDirs: List<Path>, destinationDir: Path, args: List<String>): Array<String> {
        return arrayOf(
            "java",
            "-Xms$startMemory", "-Xmx$maxMemory",
            "-classpath", formatterJar.toFile().absolutePath,    // classpath of embedded formatter jar
            "com.copperleaf.kodiak.swift.MainKt",               // Swiftdoc Formatter main class
            "--src", sourceDirs.map { it.toFile().absolutePath }
                .joinToString(separator = File.pathSeparator),   // the sources to process
            "--output", destinationDir.toFile().absolutePath,    // where Orchid will find them later
            "--cacheDir", cacheDir.toFile().absolutePath,        // where Orchid will find them later
            *args.toTypedArray()                                 // allow additional arbitrary args
        )
    }

    override fun loadModuleDocFromDisk(destinationDir: Path): SwiftModuleDoc {
        return SwiftModuleDoc(
            getDocsInSubdirectory(destinationDir, "SourceFile", SwiftSourceFile.serializer()),
            getDocsInSubdirectory(destinationDir, "Class", SwiftClass.serializer())
        )
    }

}
