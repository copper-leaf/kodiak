package com.copperleaf.kodiak.java

import com.copperleaf.kodiak.java.models.JavaClass
import com.copperleaf.kodiak.java.models.JavaPackage
import com.copperleaf.kodiak.java.models.JavaRootDoc
import com.copperleaf.kodiak.common.BaseDocInvoker
import com.copperleaf.kodiak.common.DocInvokerDescriptor
import com.copperleaf.kodiak.common.modules.ModuleLocator
import com.copperleaf.kodiak.common.modules.impl.modulelocator.GradleModuleLocator
import com.copperleaf.kodiak.common.modules.impl.modulelocator.MavenModuleLocator
import java.nio.file.Files
import java.nio.file.Path

class JavadocInvokerImpl(
        cacheDir: Path = Files.createTempDirectory("javadoc-runner"),
        private val startMemory: String = "256m",
        private val maxMemory: String = "1024m"
) : BaseDocInvoker<JavaRootDoc>(cacheDir) {
    override val formatterJarName = "javadoc-formatter-all"

    override fun describe(): DocInvokerDescriptor {
        return DocInvokerDescriptor(
            ModuleLocator.from(GradleModuleLocator(), MavenModuleLocator()),
            listOf("java")
        )
    }

    override fun createProcessArgs(sourceDirs: List<Path>, destinationDir: Path, args: List<String>): Array<String> {
        val allFiles = mutableListOf<String>()
        sourceDirs.forEach {
            it.toFile().walk().forEach { f ->
                if (f.isFile && f.extension == "java") {
                    allFiles.add(f.absolutePath)
                }
            }
        }
        return arrayOf(
            "javadoc",
            "-J-Xms$startMemory", "-J-Xmx$maxMemory",
            "-d", destinationDir.toFile().absolutePath,                 // where Orchid will find them later
            "-doclet", "com.copperleaf.kodiak.java.JavadocJsonDoclet",
            "-docletpath", formatterJar.toFile().absolutePath,          // classpath of embedded formatter jar
            *args.toTypedArray(),                                       // allow additional arbitrary args
            *allFiles.toTypedArray()                                    // the sources to process
        )
    }

    override fun loadModuleDocFromDisk(destinationDir: Path): JavaRootDoc {
        return JavaRootDoc(
            getDocsInSubdirectory(destinationDir, "Package", JavaPackage.serializer()),
            getDocsInSubdirectory(destinationDir, "Class", JavaClass.serializer())
        )
    }

}
