package com.copperleaf.kodiak.java.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.java.models.JavaEnumConstant
import com.sun.javadoc.FieldDoc

fun FieldDoc.toEnumConstant(): JavaEnumConstant {
    val modifiers = listOf(this.modifiers())
    return JavaEnumConstant(
        this,
        this.name(),
        this.qualifiedName(),
        modifiers,
        this.getComment(),
        this.enumConstantSignature()
    )
}

fun FieldDoc.enumConstantSignature() : List<CommentComponent> {
    return listOf(
        CommentComponent(TEXT, this.name())
    )
}
