package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovyMethod
import com.copperleaf.groovydoc.json.models.GroovyParameter
import com.copperleaf.groovydoc.json.models.GroovyReturnType
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import com.copperleaf.json.common.ElementType
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyMethodDoc
import org.codehaus.groovy.groovydoc.GroovyType

fun GroovyMethodDoc.toMethod(parent: GroovyClassDoc): GroovyMethod {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    val parameters = formatParameters(this.parameters(), this.findCommentTags().filter { it.name() == "parameters" })
    val returnType = this.returnType().real().toReturnType(this)
    return GroovyMethod(
        this,
        this.name(),
        this.name(),
        modifiers,
        DocComment(
            this.findCommentText(),
            emptyMap()
        ),
        parameters,
        returnType,
        this.methodSignature(
            modifiers,
            parameters,
            returnType
        )
    )
}

fun GroovyType.toReturnType(parent: GroovyMethodDoc): GroovyReturnType {
    return GroovyReturnType(
        this,
        this.simpleTypeName(),
        this.qualifiedTypeName(),
        emptyList(),
        DocComment(
            (parent.findCommentTags().firstOrNull { it.name() == "returns" }?.text() ?: "").asCommentText(),
            emptyMap()
        ),
        this.simpleTypeName(),
        this.qualifiedTypeName(),
        this.toTypeSignature()
    )
}

fun GroovyMethodDoc.methodSignature(
    modifiers: List<String>,
    parameters: List<GroovyParameter>,
    returnType: ElementType
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
//    list.addAll(this.typeParameters().toWildcardSignature())
    list.addAll(returnType.signature)
    list.add(CommentComponent("name", " ${this.name()}"))
    list.addAll(parameters.toParameterListSignature())

    return list
}