package com.copperleaf.kodiak.java.formatter

import com.caseyjbrooks.clog.Clog
import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.java.models.JavaMethod
import com.copperleaf.kodiak.java.models.JavaParameter
import com.copperleaf.kodiak.java.models.JavaReturnType
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.Tag
import com.sun.javadoc.Type

fun MethodDoc.toMethod(): JavaMethod {
    Clog.v("Formatting method: {}", this.qualifiedName())
    val modifiers = listOf(this.modifiers())
    val parameters = formatParameters(this.parameters(), this.paramTags(), this.isVarArgs)
    val returnType = this.returnType().toReturnType(this.tags().find { it.name() == "@return" })
    return JavaMethod(
        this,
        this.name(),
        this.qualifiedName(),
        modifiers,
        this.getComment(),
        parameters,
        returnType,
        this.methodSignature(
            modifiers,
            parameters,
            returnType
        )
    )
}

fun Type.toReturnType(returnTag: Tag?): JavaReturnType {
    return JavaReturnType(
        this,
        this.simpleTypeName(),
        this.qualifiedTypeName(),
        emptyList(),
        returnTag.getComment(),
        this.simpleTypeName(),
        this.qualifiedTypeName(),
        this.toTypeSignature()
    )
}

fun MethodDoc.methodSignature(
    modifiers: List<String>,
    parameters: List<JavaParameter>,
    returnType: JavaReturnType
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.addAll(this.typeParameters().toWildcardSignature())
    list.addAll(returnType.signature)
    list.add(CommentComponent("name", " ${this.name()}"))
    list.addAll(parameters.toParameterListSignature())

    return list
}