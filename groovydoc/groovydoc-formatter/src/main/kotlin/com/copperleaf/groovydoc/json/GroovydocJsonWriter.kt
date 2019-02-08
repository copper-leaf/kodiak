package com.copperleaf.groovydoc.json

import com.caseyjbrooks.clog.Clog
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyPackageDoc
import org.codehaus.groovy.groovydoc.GroovyRootDoc
import org.codehaus.groovy.tools.groovydoc.OutputTool
import java.io.File
import java.util.Properties

class GroovyDocJsonWriter(
    private val output: OutputTool,
    private val properties: Properties
) {

    private val templateEngine = GroovyDocJsonTemplateEngine()

    fun writeClasses(rootDoc: GroovyRootDoc, destdir: String) {
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
            .forEach { writeClassToOutput(it, destdir) }
    }

    private fun writeClassToOutput(classDoc: GroovyClassDoc, destdir: String) {
        val destFolder = destdir
        val destFileName = "$destFolder/${classDoc.fullPathName}.json"
        Clog.d("Generating class file $destFileName")
        output.makeOutputArea(destFolder)
        output.writeToOutput(
            destFileName,
            templateEngine.applyClassTemplates(classDoc),
            FILE_ENCODING
        )
    }

    fun writePackages(rootDoc: GroovyRootDoc, destdir: String) {
        rootDoc
            .specifiedPackages()
            .filter { !File(it.name()).isAbsolute }
            .forEach { writePackageToOutput(it, destdir) }
    }

    private fun writePackageToOutput(packageDoc: GroovyPackageDoc, destdir: String) {
        val destFolder = "$destdir/${packageDoc.name()}"
        val destFileName = "$destFolder/index.json"
        output.makeOutputArea(destFolder)
        Clog.d("Generating package file $destFileName")
        output.writeToOutput(
            destFileName,
            templateEngine.applyPackageTemplate(packageDoc),
            FILE_ENCODING
        )
    }

}
