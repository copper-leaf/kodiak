package com.copperleaf.kodiak.java.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.java.models.JavaConstructor
import com.copperleaf.kodiak.java.models.JavaParameter
import com.sun.javadoc.ConstructorDoc

fun ConstructorDoc.toConstructor(): JavaConstructor {
    val modifiers = listOf(this.modifiers())
    val parameters = formatParameters(this.parameters(), this.paramTags(), this.isVarArgs)
    return JavaConstructor(
        this,
        this.name(),
        this.qualifiedName(),
        modifiers,
        this.getComment(),
        parameters,
        this.constructorSignature(
            modifiers,
            parameters
        )
    )
}

fun ConstructorDoc.constructorSignature(
    modifiers: List<String>,
    parameters: List<JavaParameter>
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.addAll(this.typeParameters().toWildcardSignature())
    list.add(
        CommentComponent(
            TYPE_NAME,
            this.containingClass().simpleTypeName(),
            this.containingClass().qualifiedName()
        )
    )
    list.addAll(parameters.toParameterListSignature())

    return list
}