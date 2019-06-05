package com.copperleaf.javadoc.json

import com.copperleaf.javadoc.json.models.JavaClass
import com.copperleaf.javadoc.json.models.JavaPackage
import com.copperleaf.javadoc.json.models.JavaRootDoc
import com.copperleaf.json.common.BaseDocInvoker
import java.nio.file.Files
import java.nio.file.Path

class JavadocInvokerImpl(
        cacheDir: Path = Files.createTempDirectory("javadoc-runner"),
        private val startMemory: String = "256m",
        private val maxMemory: String = "1024m"
) : BaseDocInvoker<JavaRootDoc>(cacheDir) {
    override val formatterJarName = "javadoc-formatter-all"

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
            "-doclet", "com.copperleaf.javadoc.json.JavadocJsonDoclet",
            "-docletpath", formatterJar.toFile().absolutePath,          // classpath of embedded formatter jar
            *args.toTypedArray(),                                       // allow additional arbitrary args
            *allFiles.toTypedArray()                                    // the sources to process
        )
    }

    override fun loadModuleDocFromDisk(destinationDir: Path): JavaRootDoc {
        val packages = getJavadocPackagePages(destinationDir)
        val classes = getJavadocClassPages(destinationDir)

        return JavaRootDoc(
            packages,
            classes
        )
    }

// Process Javadoc output to a model Orchid can use
//----------------------------------------------------------------------------------------------------------------------

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
