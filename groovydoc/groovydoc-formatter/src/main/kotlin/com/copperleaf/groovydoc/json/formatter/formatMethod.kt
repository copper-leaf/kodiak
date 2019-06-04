package com.copperleaf.groovydoc.json.formatter

import com.copperleaf.groovydoc.json.models.GroovydocMethod
import com.copperleaf.groovydoc.json.models.GroovydocParameter
import com.copperleaf.groovydoc.json.models.GroovydocReturnType
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.ElementType
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyMethodDoc
import org.codehaus.groovy.groovydoc.GroovyType

fun GroovyMethodDoc.toMethod(parent: GroovyClassDoc): GroovydocMethod {
    val modifiers = listOf(this.modifiers()).filterNotNull()
    val parameters = formatParameters(this.parameters(), this.findCommentTags().filter { it.name() == "parameters" })
    val returnType = this.returnType().real().toReturnType(this)
    return GroovydocMethod(
        this,
        this.name(),
        this.name(),
        this.findCommentText(),
        emptyMap(),
        modifiers,
        parameters,
        returnType,
        this.methodSignature(
            modifiers,
            parameters,
            returnType
        )
    )
}

fun GroovyType.toReturnType(parent: GroovyMethodDoc): GroovydocReturnType {
    return GroovydocReturnType(
        this,
        this.simpleTypeName(),
        this.qualifiedTypeName(),
        (parent.findCommentTags().firstOrNull { it.name() == "returns" }?.text() ?: "").asCommentText(),
        emptyMap(),
        this.simpleTypeName(),
        this.qualifiedTypeName(),
        this.toTypeSignature()
    )
}

fun GroovyMethodDoc.methodSignature(
    modifiers: List<String>,
    parameters: List<GroovydocParameter>,
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