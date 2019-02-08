package com.copperleaf.groovydoc.json

import org.codehaus.groovy.groovydoc.GroovyRootDoc
import org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager
import org.codehaus.groovy.tools.groovydoc.GroovyDocTool
import org.codehaus.groovy.tools.groovydoc.GroovyRootDocBuilder
import org.codehaus.groovy.tools.groovydoc.OutputTool
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

    private val rootDocBuilder = GroovyRootDocBuilder(this, sourcepaths, emptyList(), properties)

    override fun getRootDoc(): GroovyRootDoc {
        return rootDocBuilder.rootDoc
    }

    override fun add(filenames: List<String>) {
        rootDocBuilder.buildTree(filenames)
    }

    override fun renderToOutput(output: OutputTool, destdir: String) {
        val writer = GroovyDocJsonWriter(output, properties)
        val rootDoc = rootDocBuilder.rootDoc
        writer.writePackages(rootDoc, destdir)
        writer.writeClasses(rootDoc, destdir)
    }

}
