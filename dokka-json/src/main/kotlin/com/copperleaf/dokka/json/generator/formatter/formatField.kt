package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinField
import com.copperleaf.dokka.json.models.SignatureComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.isField: Boolean get() = this.kind == NodeKind.Field || this.kind == NodeKind.Property

fun DocumentationNode.toField(): KotlinField {
    assert(this.isField) { "node must be a Field or Property" }
    val modifiers = this.modifiers
    return KotlinField(
            this,
            this.simpleName,
            this.qualifiedName,
            this.contentText,
            this.summary.textLength,
            modifiers,
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
): List<SignatureComponent> {
    val list = mutableListOf<SignatureComponent>()

    list.addAll(modifiers.toModifierListSignature())
    list.add(SignatureComponent("name", this.simpleName, ""))
    list.add(SignatureComponent("punctuation", ": ", ""))
    list.addAll(type.asType().toTypeSignature())

    return list
}