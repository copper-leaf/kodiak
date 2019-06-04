package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocPackageDoc
import org.codehaus.groovy.groovydoc.GroovyPackageDoc

fun GroovyPackageDoc.toPackageDoc(): GroovydocPackageDoc {
    return GroovydocPackageDoc(
        this,
        this.nameWithDots(),
        this.nameWithDots(),
        this.description().asCommentText(),
        emptyMap(),
        this.allClasses().map { it.toClassDoc(false) }
    )
}