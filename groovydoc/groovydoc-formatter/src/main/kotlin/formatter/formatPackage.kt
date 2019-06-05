package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovyPackage
import com.copperleaf.json.common.DocComment
import org.codehaus.groovy.groovydoc.GroovyPackageDoc

fun GroovyPackageDoc.toPackageDoc(): GroovyPackage {
    return GroovyPackage(
        this,
        this.nameWithDots(),
        this.nameWithDots(),
        emptyList(),
        DocComment(
            this.description().asCommentText(),
            emptyMap()
        ),
        this.allClasses().map { it.toClassDoc(false) }
    )
}