package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocClassDoc
import org.codehaus.groovy.groovydoc.GroovyClassDoc

fun GroovyClassDoc.toClassDoc(): GroovydocClassDoc {
    return GroovydocClassDoc(
        this,
        this.name()
    )
}