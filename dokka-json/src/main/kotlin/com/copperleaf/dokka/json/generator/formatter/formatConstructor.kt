package com.copperleaf.dokka.json.generator.formatter

import com.copperleaf.dokka.json.models.KotlinConstructor
import com.copperleaf.dokka.json.models.KotlinParameter
import com.copperleaf.dokka.json.models.SignatureComponent
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.NodeKind

val DocumentationNode.isConstructor: Boolean get() = this.kind == NodeKind.Constructor

fun DocumentationNode.toConstructor(): KotlinConstructor {
    assert(this.isConstructor) { "node must be a Constructor" }
    val modifiers = this.modifiers
    val parameters = this.parameters
    return KotlinConstructor(
            this.simpleName,
            this.qualifiedName,
            this.contentText,
            this.summary.textLength,
            modifiers,
            parameters,
            this.constructorSignature(
                    modifiers,
                    parameters
            )
    )
}

fun DocumentationNode.constructorSignature(
        modifiers: List<String>,
        parameters: List<KotlinParameter>
): List<SignatureComponent> {
    val signatureComponents = mutableListOf<SignatureComponent>()

    for (modifier in modifiers) {
        signatureComponents.add(SignatureComponent("modifier", "$modifier ", ""))
    }
    signatureComponents.add(SignatureComponent("keyword", "constructor", ""))

    signatureComponents.add(SignatureComponent("punctuation", "(", ""))
    parameters.forEachIndexed { index, parameter ->
        signatureComponents.add(SignatureComponent("name", parameter.name, ""))
        signatureComponents.add(SignatureComponent("punctuation", ": ", ""))
        signatureComponents.add(SignatureComponent("type", parameter.type, parameter.qualifiedType))

        if (index < parameters.size - 1) {
            signatureComponents.add(SignatureComponent("punctuation", ", ", ""))
        }
    }
    signatureComponents.add(SignatureComponent("punctuation", ")", ""))

    return signatureComponents
}