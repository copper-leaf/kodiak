package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocField
import com.copperleaf.json.common.CommentComponent
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyFieldDoc
import org.codehaus.groovy.groovydoc.GroovyType

fun GroovyFieldDoc.toField(parent: GroovyClassDoc): GroovydocField {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    return GroovydocField(
            this,
            this.name(),
            this.name(),
            this.findCommentText(),
            modifiers,
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