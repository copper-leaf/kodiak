package com.copperleaf.groovydoc.json

import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyPackageDoc

class GroovyDocJsonTemplateEngine {

    fun applyClassTemplates(classDoc: GroovyClassDoc): String {
        return """
            {
                "name": "class file: ${classDoc.name()}"
            }
        """.trimIndent()
    }

    fun applyPackageTemplate(packageDoc: GroovyPackageDoc): String {
        return """
            {
                "name": "package file: ${packageDoc.name()}"
            }
        """.trimIndent()
    }


}
