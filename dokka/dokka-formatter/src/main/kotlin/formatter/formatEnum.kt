package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.kotlin.models.KotlinEnumConstant
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION

val DocumentationNode.isEnumItem: Boolean get() = this.kind == NodeKind.EnumItem

fun DocumentationNode.toEnumConstantDoc(): KotlinEnumConstant {
    assert(this.isEnumItem) { "node must be an EnumItem" }
    val modifiers = this.modifiers
    return KotlinEnumConstant(
        this,
        this.simpleName,
        this.qualifiedName,
        modifiers,
        this.getComment(),
        this.enumConstantSignature()
    )
}

fun DocumentationNode.enumConstantSignature() : List<CommentComponent> {
    return listOf(
        CommentComponent(TEXT, this.simpleName)
    )
}
