package com.copperleaf.kodiak.groovy.formatter

import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.groovy.models.GroovyPackage
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