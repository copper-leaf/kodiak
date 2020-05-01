package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.kotlin.models.KotlinField
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind
import com.copperleaf.kodiak.common.CommentComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.CommentComponent.Companion.TEXT
import com.copperleaf.kodiak.common.CommentComponent.Companion.PUNCTUATION

val DocumentationNode.isField: Boolean get() = this.kind in listOf(NodeKind.Field, NodeKind.Property)
val DocumentationNode.isCompanionField: Boolean get() = this.kind in listOf(NodeKind.CompanionObjectProperty)

fun DocumentationNode.toField(): KotlinField {
    assert(this.isField || this.isCompanionField) { "node must be a Field or Property" }
    val modifiers = this.modifiers
    return KotlinField(
        this,
        this.simpleName,
        this.qualifiedName,
        modifiers,
        this.getComment(),
        this.simpleType,
        this.qualifiedType,
        this.fieldSignature(
            modifiers,
            this
        )
    )
}

fun DocumentationNode.fieldSignature(
    modifiers: List<String>,
    type: DocumentationNode
): List<CommentComponent> {
    val list = mutableListOf<CommentComponent>()

    if(modifiers.isEmpty()) {
        list.add(CommentComponent(TEXT, "val "))
    }
    else {
        list.addAll(modifiers.toModifierListSignature())
    }

    list.add(CommentComponent(TEXT, this.simpleName))
    list.add(CommentComponent(PUNCTUATION, ": "))
    list.addAll(type.asType().toTypeSignature())

    return list
}
