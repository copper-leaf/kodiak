package com.copperleaf.groovydoc.json

import org.apache.tools.ant.BuildException
import org.codehaus.groovy.tools.groovydoc.FileOutputTool
import java.nio.file.Path
import java.util.Properties

class GroovydocJsonFormatter(
    private val dirs: List<Path>,
    private val destDir: Path
) {

    var scope = "public"
    var processScripts = true

    private var extensions = listOf("java", "groovy", "gv", "gvy", "gsh")

    private fun getRelativeSourceFiles(): List<String> {
        return dirs
            .map { dir ->
                dir.toFile()
                    .walkTopDown()
                    .filter { it.exists() && it.isFile }
                    .filter { extensions.contains(it.extension) }
                    .map {
                        val basePath = dir.toFile()
                        val sourceFilePath = it
                        val relativePath = sourceFilePath.relativeTo(basePath)
                        relativePath.invariantSeparatorsPath
                    }
                    .toList()
            }
            .flatten()
            .distinct()
    }

    @Throws(BuildException::class)
    fun execute() {
        val properties = Properties().apply {
            setProperty("publicScope", (scope == "public").toString())
            setProperty("protectedScope", (scope == "protected").toString())
            setProperty("packageScope", (scope == "package").toString())
            setProperty("privateScope", (scope == "private").toString())
            setProperty("processScripts", processScripts.toString())
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
