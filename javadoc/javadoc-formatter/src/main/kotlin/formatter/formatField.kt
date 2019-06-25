package com.copperleaf.kodiak.java.formatter

import com.copperleaf.kodiak.java.models.JavaField
import com.copperleaf.kodiak.common.CommentComponent
import com.sun.javadoc.FieldDoc
import com.sun.javadoc.Type

fun FieldDoc.toField(): JavaField {
    val modifiers = listOf(this.modifiers())
    return JavaField(
        this,
        this.name(),
        this.qualifiedName(),
        modifiers,
        this.getComment(),
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