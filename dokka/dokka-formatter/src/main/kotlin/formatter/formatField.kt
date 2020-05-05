package com.copperleaf.kodiak.kotlin.formatter

import com.copperleaf.kodiak.common.RichTextComponent
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TEXT
import com.copperleaf.kodiak.common.RichTextComponent.Companion.TYPE_NAME
import com.copperleaf.kodiak.common.RichTextComponent.Companion.PUNCTUATION
import com.copperleaf.kodiak.common.RichTextComponent.Companion.INHERITED
import com.copperleaf.kodiak.common.RichTextComponent.Companion.COMPOSED
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
): List<RichTextComponent> {
    val list = mutableListOf<RichTextComponent>()

    if(modifiers.isEmpty()) {
        list.add(RichTextComponent(TEXT, "val "))
    }
    else {
        list.addAll(modifiers.toModifierListSignature())
    }

    list.add(RichTextComponent(TEXT, this.simpleName))
    list.add(RichTextComponent(PUNCTUATION, ": "))
    list.addAll(type.asType().toTypeSignature())

    return list
}
