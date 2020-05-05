package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.INHERITED
import com.copperleaf.kodiak.common.RichTextComponent.Companion.COMPOSED
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

fun DocumentationNode.typealiasSignature(): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()
    val underlyingKind = this.details.first { it.kind == NodeKind.TypeAliasUnderlyingType }

    list.add(RichTextComponent(TEXT, "typealias "))
    list.add(RichTextComponent(TYPE_NAME, this.simpleName, this.qualifiedName))
    list.add(RichTextComponent(PUNCTUATION, " = "))
    list.add(
        RichTextComponent(
            TYPE_NAME,
            underlyingKind.simpleName,
            underlyingKind.detailOrNull(NodeKind.QualifiedName)?.simpleName ?: ""
        )
    )

    return list
}
