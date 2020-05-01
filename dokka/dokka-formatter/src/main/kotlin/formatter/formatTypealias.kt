package com.copperleaf.kodiak.kotlin.formatter

import com.caseyjbrooks.clog.Clog
import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.kotlin.models.KotlinTypealias
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.isTypealias: Boolean get() = this.kind == NodeKind.TypeAlias

fun DocumentationNode.toTypealiasDoc(): KotlinTypealias {
    assert(this.isTypealias) { "node must be a Typealias" }

    val modifiers = this.modifiers

    return KotlinTypealias(
        this,
        this.simpleName,
        this.qualifiedName,
        modifiers,
        this.getComment(),
        this.typealiasSignature()
    )
}

fun DocumentationNode.typealiasSignature(): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()
    val underlyingKind = this.details.first { it.kind == NodeKind.TypeAliasUnderlyingType }

    list.add(CommentComponent(TEXT, "typealias "))
    list.add(CommentComponent(TYPE_NAME, this.simpleName, this.qualifiedName))
    list.add(CommentComponent(PUNCTUATION, " = "))
    list.add(
        CommentComponent(
            TYPE_NAME,
            underlyingKind.simpleName,
            underlyingKind.detailOrNull(NodeKind.QualifiedName)?.simpleName ?: ""
        )
    )

    return list
}
