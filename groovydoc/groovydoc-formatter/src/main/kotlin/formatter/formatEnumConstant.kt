package com.copperleaf.kodiak.groovy.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.groovy.models.GroovyEnumConstant
import org.codehaus.groovy.groovydoc.GroovyFieldDoc

fun GroovyFieldDoc.toEnumConstant(): GroovyEnumConstant {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    return GroovyEnumConstant(
        this,
        this.name(),
        this.name(),
        modifiers,
        this.getComment(),
        this.enumConstantSignature()
    )
}

fun GroovyFieldDoc.enumConstantSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.add(CommentComponent("text", this.name()))

    return list
}