package com.copperleaf.kodiak.groovy

import com.caseyjbrooks.clog.Clog
import com.copperleaf.kodiak.common.connectAllToParents
import com.copperleaf.kodiak.groovy.formatter.toClassDoc
import com.copperleaf.kodiak.groovy.formatter.toPackageDoc
import com.copperleaf.kodiak.groovy.models.GroovyClass
import com.copperleaf.kodiak.groovy.models.GroovyPackage
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
            .map { it.toClassDoc(true) }
            .forEach { writeClassToOutput(output, it, destdir) }
    }

    private fun writeClassToOutput(output: OutputTool, classDoc: GroovyClass, destdir: String) {
        val destFolder = destdir
        val destFileName = "$destFolder/Class/${classDoc.id.replace('.', '/')}.json"
        Clog.d("Generating class file $destFileName")
        output.makeOutputArea(destFolder)
        output.writeToOutput(
            destFileName,
            classDoc.toJson(),
            FILE_ENCODING
        )
    }

    private fun writePackages(output: OutputTool, rootDoc: GroovyRootDoc, destdir: String) {
        connectAllToParents(
            // create initial packages, use common functionality to connect parent-child structures
            rootDoc
                .specifiedPackages()
                .filter { !File(it.name()).isAbsolute }
                .map { it.toPackageDoc(true) },
            {
                it.item.copy(
                    parent = it.parentId ?: "",
                    subpackages = it.children.map { (it.item.node as GroovyPackageDoc).toPackageDoc(false) }
                )
            },
            { it.id }
        ).forEach {
            // write package files to disk
            writePackageToOutput(output, it, destdir)
        }
    }

    private fun writePackageToOutput(output: OutputTool, packageDoc: GroovyPackage, destdir: String) {
        val destFolder = "$destdir/Package/${packageDoc.id.replace('.', '/')}"
        val destFileName = "$destFolder/index.json"
        output.makeOutputArea(destFolder)
        Clog.d("Generating package file $destFileName")
        output.writeToOutput(
            destFileName,
            packageDoc.toJson(),
            FILE_ENCODING
        )
    }

}
