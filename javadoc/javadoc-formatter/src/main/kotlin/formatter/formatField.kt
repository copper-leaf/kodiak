package com.copperleaf.javadoc.json.formatter

import com.copperleaf.javadoc.json.models.JavaField
import com.copperleaf.json.common.CommentComponent
import com.copperleaf.json.common.DocComment
import com.sun.javadoc.FieldDoc
import com.sun.javadoc.Type

fun FieldDoc.toField(): JavaField {
    val modifiers = listOf(this.modifiers())
    return JavaField(
        this,
        this.name(),
        this.qualifiedName(),
        modifiers,
        DocComment(
            this.inlineTags().asCommentComponents(),
            this.tags().asCommentComponentsMap()
        ),
        this.type().simpleTypeName(),
        this.type().qualifiedTypeName(),
        this.fieldSignature(
            modifiers,
            this.type()
        )
    )
}

fun FieldDoc.fieldSignature(
    modifiers: List<String>,
    type: Type
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.addAll(type.toTypeSignature())
    list.add(CommentComponent("name", " ${this.name()}"))

    return list
}