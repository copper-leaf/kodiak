package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.CommentComponent
import com.copperleaf.kodiak.kotlin.models.KotlinField
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

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
        list.add(CommentComponent("modifier", "val "))
    }
    else {
        list.addAll(modifiers.toModifierListSignature())
    }

    list.add(CommentComponent("name", this.simpleName))
    list.add(CommentComponent("punctuation", ": "))
    list.addAll(type.asType().toTypeSignature())

    return list
}