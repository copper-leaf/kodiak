package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovyField
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyFieldDoc
import org.codehaus.groovy.groovydoc.GroovyType

fun GroovyFieldDoc.toField(parent: GroovyClassDoc): GroovyField {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    return GroovyField(
        this,
        this.name(),
        this.name(),
        modifiers,
        DocComment(
            this.findCommentText(),
            emptyMap()
        ),
        this.type().real().simpleTypeName(),
        this.type().real().qualifiedTypeName(),
        this.fieldSignature(
            modifiers,
            this.type()
        )
    )
}

fun GroovyFieldDoc.fieldSignature(
    modifiers: List<String>,
    type: GroovyType
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.addAll(type.toTypeSignature())
    list.add(CommentComponent("name", " ${this.name()}"))

    return list
}