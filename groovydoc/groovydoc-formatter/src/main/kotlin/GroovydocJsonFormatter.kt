package com.copperleaf.kodiak.groovy

import org.codehaus.groovy.tools.groovydoc.FileOutputTool
import java.nio.file.Path
import java.util.Properties

class GroovydocJsonFormatter(
    private val dirs: List<Path>,
    private val destDir: Path
) {

    var scope = "public"

    private var extensions = listOf("java", "groovy", "gv", "gvy", "gsh")

    private fun getRelativeSourceFiles(): List<String> {
        return dirs
            .map { dir ->
                dir.toFile()
                    .walkTopDown() // recursively find files in the source directory
                    .filter { it.exists() && it.isFile } // only get files, not directories
                    .filter { extensions.contains(it.extension) } // only use Groovy or Java files
                    .map { it.relativeTo(dir.toFile()).invariantSeparatorsPath } // get the file's path relative to the source directory
                    .toList()
            }
            .flatten()
            .distinct()
    }

    fun execute() {
        val properties = Properties().apply {
            setProperty("publicScope", (scope == "public").toString())
            setProperty("protectedScope", (scope == "protected").toString())
            setProperty("packageScope", (scope == "package").toString())
            setProperty("privateScope", (scope == "private").toString())
            setProperty("processScripts", "true")
            setProperty("includeMainForScripts", "false")
            setProperty("charset", FILE_ENCODING)
            setProperty("fileEncoding", FILE_ENCODING)

            // expect just one scope to be set on the way in but now also set higher levels of visibility
            if ("true" == getProperty("privateScope")) setProperty("packageScope", "true")
            if ("true" == getProperty("packageScope")) setProperty("protectedScope", "true")
            if ("true" == getProperty("protectedScope")) setProperty("publicScope", "true")
        }

        val sourceDirs = dirs.map { it.toFile().absolutePath }.toTypedArray()
        val sourceFiles = getRelativeSourceFiles()

        GroovyDocJsonTool(sourceDirs, properties).let {
            it.add(sourceFiles)
            it.renderToOutput(FileOutputTool(), destDir.toFile().canonicalPath)
        }
    }
}
