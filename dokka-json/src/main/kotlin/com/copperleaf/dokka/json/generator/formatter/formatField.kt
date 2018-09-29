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
            this.simpleName,
            this.qualifiedName,
            this.contentText,
            this.summary.textLength,
            modifiers,
            this.simpleType,
            this.qualifiedType,
            this.fieldSignature(modifiers)
    )
}

fun DocumentationNode.fieldSignature(
        modifiers: List<String>
): List<SignatureComponent> {
    val signatureComponents = mutableListOf<SignatureComponent>()

    for (modifier in modifiers) {
        signatureComponents.add(SignatureComponent("modifier", "$modifier ", ""))
    }
    signatureComponents.add(SignatureComponent("name", this.simpleName, ""))
    signatureComponents.add(SignatureComponent("punctuation", ": ", ""))
    signatureComponents.add(SignatureComponent("type", this.simpleType, this.qualifiedType))

    return signatureComponents
}