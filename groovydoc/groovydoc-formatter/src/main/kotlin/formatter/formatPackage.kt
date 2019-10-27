package com.copperleaf.kodiak.groovy.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.DocComment
import com.copperleaf.kodiak.groovy.models.GroovyPackage
import org.codehaus.groovy.groovydoc.GroovyPackageDoc

fun GroovyPackageDoc.toPackageDoc(deep: Boolean = true): GroovyPackage {
    return GroovyPackage(
        this,
        this.nameWithDots(),
        this.nameWithDots(),
        "",
        emptyList(),
        DocComment(
            this.description().parseCommentToValues().commentText,
            emptyMap()
        ),
        this.packageSignature(),
        this.allClasses().map { it.toClassDoc(false) },
        emptyList()
    )
}

fun GroovyPackageDoc.packageSignature(): List<CommentComponent> {
    return listOf(
        CommentComponent("keyword", "package "),
        CommentComponent(TYPE_NAME, this.nameWithDots(), this.nameWithDots())
    )
}
