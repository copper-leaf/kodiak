package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaConstructor
import com.copperleaf.javadoc.json.models.JavaParameter
import com.copperleaf.json.common.CommentComponent
import com.sun.javadoc.ConstructorDoc

fun ConstructorDoc.toConstructor(): JavaConstructor {
    val modifiers = listOf(this.modifiers())
    val parameters = formatParameters(this.parameters(), this.paramTags())
    return JavaConstructor(
            this,
            this.name(),
            this.qualifiedName(),
            this.commentText(),
            this.inlineTags().asCommentComponents(),
            this.tags().asCommentComponentsMap(),
            modifiers,
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
    list.add(CommentComponent("type", this.containingClass().simpleTypeName(), this.containingClass().qualifiedName()))
    list.addAll(parameters.toParameterListSignature())

    return list
}