package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaMethod
import com.copperleaf.javadoc.json.models.JavaParameter
import com.copperleaf.javadoc.json.models.JavaReturnType
import com.copperleaf.javadoc.json.models.SignatureComponent
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.Tag
import com.sun.javadoc.Type

fun MethodDoc.toMethod(): JavaMethod {
    val modifiers = listOf(this.modifiers())
    val parameters = formatParameters(this.parameters(), this.paramTags())
    val returnType = this.returnType().toReturnType(this.tags().find { it.name() == "@return" })
    return JavaMethod(
            this,
            this.name(),
            this.qualifiedName(),
            this.commentText(),
            this.inlineTags().asCommentTags(),
            this.tags().asCommentTagsMap(),
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

fun Type.toReturnType(returnTag: Tag?): JavaReturnType {
    return JavaReturnType(
            this,
            this.simpleTypeName(),
            this.qualifiedTypeName(),
            returnTag?.text() ?: "",
            if (returnTag != null) arrayOf(returnTag).asCommentTags() else emptyList(),
            if (returnTag != null) arrayOf(returnTag).asCommentTagsMap() else emptyMap(),
            this.simpleTypeName(),
            this.qualifiedTypeName(),
            this.toTypeSignature()
    )
}

fun MethodDoc.methodSignature(
        modifiers: List<String>,
        parameters: List<JavaParameter>,
        returnType: JavaReturnType
): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.addAll(this.typeParameters().toWildcardSignature())
    list.addAll(returnType.signature)
    list.add(SignatureComponent("name", " ${this.name()}", ""))
    list.addAll(parameters.toParameterListSignature())

    return list
}