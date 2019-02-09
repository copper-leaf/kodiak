package com.copperleaf.groovydoc.json

import com.caseyjbrooks.clog.Clog
import com.copperleaf.groovydoc.json.formatter.toClassDoc
import com.copperleaf.groovydoc.json.formatter.toPackageDoc
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyPackageDoc
import org.codehaus.groovy.groovydoc.GroovyRootDoc
import org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager
import org.codehaus.groovy.tools.groovydoc.GroovyDocTool
import org.codehaus.groovy.tools.groovydoc.GroovyRootDocBuilder
import org.codehaus.groovy.tools.groovydoc.OutputTool
import java.io.File
import java.util.Properties

class GroovyDocJsonTool(
    sourcepaths: Array<String>,
    properties: Properties
) : GroovyDocTool(
    ClasspathResourceManager(),
    sourcepaths,
    emptyArray(),
    emptyArray(),
    emptyArray(),
    emptyList(),
    properties
) {

// Get RootDoc model from Groovy APIs
//----------------------------------------------------------------------------------------------------------------------

    private val rootDocBuilder = GroovyRootDocBuilder(this, sourcepaths, emptyList(), properties)

    override fun getRootDoc(): GroovyRootDoc {
        return rootDocBuilder.rootDoc
    }

    override fun add(filenames: List<String>) {
        rootDocBuilder.buildTree(filenames)
    }

    override fun renderToOutput(output: OutputTool, destdir: String) {
        val rootDoc = rootDocBuilder.rootDoc
        writePackages(output, rootDoc, destdir)
        writeClasses(output, rootDoc, destdir)
    }

// Write classes and packages to JSON files
//----------------------------------------------------------------------------------------------------------------------

    private fun writeClasses(output: OutputTool, rootDoc: GroovyRootDoc, destdir: String) {
        rootDoc
            .classes()
            .filter {
                when {
                    (it.isPublic)                                                                             -> true
                    (it.isProtected      && (properties.getProperty("protectedScope")?.toBoolean() ?: false)) -> true
                    (it.isPackagePrivate && (properties.getProperty("packageScope")?.toBoolean()   ?: false)) -> true
                    (it.isPrivate        && (properties.getProperty("privateScope")?.toBoolean()   ?: false)) -> true
                    else                                                                                      -> false
                }
            }
            .forEach { writeClassToOutput(output, it, destdir) }
    }

    private fun writeClassToOutput(output: OutputTool, classDoc: GroovyClassDoc, destdir: String) {
        val destFolder = destdir
        val destFileName = "$destFolder/${classDoc.fullPathName}.json"
        Clog.d("Generating class file $destFileName")
        output.makeOutputArea(destFolder)
        output.writeToOutput(
            destFileName,
            classDoc.toClassDoc().toJson(),
            FILE_ENCODING
        )
    }

    private fun writePackages(output: OutputTool, rootDoc: GroovyRootDoc, destdir: String) {
        rootDoc
            .specifiedPackages()
            .filter { !File(it.name()).isAbsolute }
            .forEach { writePackageToOutput(output, it, destdir) }
    }

    private fun writePackageToOutput(output: OutputTool, packageDoc: GroovyPackageDoc, destdir: String) {
        val destFolder = "$destdir/${packageDoc.name()}"
        val destFileName = "$destFolder/index.json"
        output.makeOutputArea(destFolder)
        Clog.d("Generating package file $destFileName")
        output.writeToOutput(
            destFileName,
            packageDoc.toPackageDoc().toJson(),
            FILE_ENCODING
        )
    }

}
